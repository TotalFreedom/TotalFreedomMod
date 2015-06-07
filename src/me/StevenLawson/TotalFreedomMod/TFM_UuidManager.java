package me.StevenLawson.TotalFreedomMod;

import com.google.common.collect.ImmutableList;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import me.StevenLawson.TotalFreedomMod.SQL.TFM_SqlUtil;
import me.StevenLawson.TotalFreedomMod.SQL.TFM_SqliteDatabase;
import me.StevenLawson.TotalFreedomMod.SQL.TFM_SqliteDatabase.Statement;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TFM_UuidManager {

    public static final String TABLE_NAME = "uuids";
    private static final TFM_SqliteDatabase SQL;
    private static final Statement FIND;
    private static final Statement UPDATE;

    private TFM_UuidManager() {
        throw new AssertionError();
    }

    static {
        SQL = new TFM_SqliteDatabase(
                "uuids.db",
                TABLE_NAME,
                "username VARCHAR(" + TotalFreedomMod.MAX_USERNAME_LENGTH + ") NOT NULL PRIMARY KEY, uuid CHAR(36) NOT NULL");

        FIND = SQL.addPreparedStatement("SELECT * FROM " + TABLE_NAME + " WHERE lower(username) = ?;");
        UPDATE = SQL.addPreparedStatement("REPLACE INTO " + TABLE_NAME + " (username, uuid) VALUES (?, ?);");
    }

    public static void load() {
        // Init DB
        SQL.connect();
    }

    public static void close() {
        SQL.close();
    }

    public static int purge() {
        return SQL.purge();
    }

    public static UUID newPlayer(Player player, String ip) {
        TFM_Log.info("Obtaining UUID for new player: " + player.getName());

        final String username = player.getName().toLowerCase();

        // Look in DB
        final UUID dbUuid = find(username);
        if (dbUuid != null) {
            return dbUuid;
        }

        // Find UUID and update in DB if not found
        // Try API
        UUID uuid = TFM_UuidResolver.getUUIDOf(username);
        if (uuid == null) {
            // Spoof
            uuid = generateSpoofUuid(username);
        }

        update(username, uuid);
        return uuid;
    }

    public static UUID getUniqueId(OfflinePlayer offlinePlayer) {
        // Online check first
        if (offlinePlayer.isOnline() && TFM_PlayerData.hasPlayerData(offlinePlayer.getPlayer())) {
            return TFM_PlayerData.getPlayerData(offlinePlayer.getPlayer()).getUniqueId();
        }

        // DB, API, Spoof
        return getUniqueId(offlinePlayer.getName());
    }

    public static UUID getUniqueId(String username) {
        // Look in DB
        final UUID dbUuid = find(username);
        if (dbUuid != null) {
            return dbUuid;
        }

        // Try API
        final UUID apiUuid = TFM_UuidResolver.getUUIDOf(username);
        if (apiUuid != null) {
            return apiUuid;
        }

        // Spoof
        return generateSpoofUuid(username);
    }

    public static void rawSetUUID(String name, UUID uuid) {
        if (name == null || uuid == null || name.isEmpty()) {
            TFM_Log.warning("Not setting raw UUID: name and uuid may not be null!");
            return;
        }

        update(name.toLowerCase().trim(), uuid);
    }

    private static UUID find(String searchName) {
        if (!SQL.connect()) {
            return null;
        }

        final ResultSet result;
        try {
            final PreparedStatement statement = FIND.getStatement();
            statement.clearParameters();
            statement.setString(1, searchName.toLowerCase());
            result = statement.executeQuery();
        } catch (Exception ex) {
            TFM_Log.severe("Could not execute find statement!");
            TFM_Log.severe(ex);
            return null;
        }

        if (!TFM_SqlUtil.hasData(result)) {
            TFM_SqlUtil.close(result);
            return null;
        }

        try {
            final String uuidString = result.getString("uuid");
            return UUID.fromString(uuidString);
        } catch (Exception ex) {
            TFM_Log.severe(ex);
            return null;
        } finally {
            TFM_SqlUtil.close(result);
        }
    }

    private static boolean update(String username, UUID uuid) {
        if (!SQL.connect()) {
            return false;
        }

        try {
            final PreparedStatement statement = UPDATE.getStatement();
            statement.clearParameters();
            statement.setString(1, username.toLowerCase());
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            return true;
        } catch (Exception ex) {
            TFM_Log.severe("Could not execute update statement!");
            TFM_Log.severe(ex);
            return false;
        }
    }

    private static UUID generateSpoofUuid(String name) {
        name = name.toLowerCase();
        TFM_Log.info("Generating spoof UUID for " + name);

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA1");
            final byte[] result = digest.digest(name.getBytes());
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < result.length; i++) {
                builder.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }

            return UUID.fromString(
                    "deadbeef"
                    + "-" + builder.substring(8, 12)
                    + "-" + builder.substring(12, 16)
                    + "-" + builder.substring(16, 20)
                    + "-" + builder.substring(20, 32));
        } catch (NoSuchAlgorithmException ex) {
            TFM_Log.warning("Could not generate spoof UUID: SHA1 algorithm not found!");
        }

        return UUID.randomUUID();
    }

    public static class TFM_UuidResolver implements Callable<Map<String, UUID>> {

        private static final double PROFILES_PER_REQUEST = 100;
        private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
        private final JSONParser jsonParser = new JSONParser();
        private final List<String> names;

        public TFM_UuidResolver(List<String> names) {
            this.names = ImmutableList.copyOf(names);
        }

        @Override
        public Map<String, UUID> call() {
            final Map<String, UUID> uuidMap = new HashMap<String, UUID>();
            int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);
            for (int i = 0; i < requests; i++) {
                try {
                    final URL url = new URL(PROFILE_URL);
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    final String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));

                    final OutputStream stream = connection.getOutputStream();
                    stream.write(body.getBytes());
                    stream.flush();
                    stream.close();

                    final JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));

                    for (Object profile : array) {
                        final JSONObject jsonProfile = (JSONObject) profile;
                        final String id = (String) jsonProfile.get("id");
                        final String name = (String) jsonProfile.get("name");
                        final UUID uuid = UUID.fromString(
                                id.substring(0, 8)
                                + "-" + id.substring(8, 12)
                                + "-" + id.substring(12, 16)
                                + "-" + id.substring(16, 20)
                                + "-" + id.substring(20, 32));
                        uuidMap.put(name, uuid);
                    }

                    if (i != requests - 1) {
                        Thread.sleep(100L);
                    }
                } catch (Exception ex) {
                    TFM_Log.severe("Could not resolve UUID(s) of "
                            + StringUtils.join(names.subList(i * 100, Math.min((i + 1) * 100, names.size())), ", "));
                    //TFM_Log.severe(ex);
                }
            }
            return uuidMap;
        }

        public static UUID getUUIDOf(String name) {
            return new TFM_UuidResolver(Arrays.asList(name)).call().get(name);
        }
    }

}
