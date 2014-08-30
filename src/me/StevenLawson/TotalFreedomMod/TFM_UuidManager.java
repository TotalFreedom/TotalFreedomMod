package me.StevenLawson.TotalFreedomMod;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import org.bukkit.OfflinePlayer;

public class TFM_UuidManager
{
    private static final Map<String, UUID> UUID_CACHE = new HashMap<String, UUID>();

    private TFM_UuidManager()
    {
        throw new AssertionError();
    }

    public static void load()
    {
        UUID_CACHE.clear();

        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, "uuid.yml", false);
        config.load();

        if (!config.isList("cache"))
        {
            config.set("cache", new ArrayList<String>());
            config.save();
            return;
        }

        for (String cache : config.getStringList("cache"))
        {
            final String[] parts = cache.split(":");
            if (parts.length != 2)
            {
                TFM_Log.warning("Invalid cached UUID: " + cache);
                continue;
            }

            final String playerName = parts[0];
            final String uuidString = parts[1];

            if (!isUniqueId(uuidString))
            {
                TFM_Log.warning("Invalid cached UUID: " + cache);
                continue;
            }

            if (uuidString.startsWith("deadbeef"))
            {
                continue;
            }

            UUID_CACHE.put(playerName.toLowerCase(), UUID.fromString(uuidString));
        }

        TFM_Log.info("Cached " + UUID_CACHE.size() + " UUIDs");
    }

    public static void save()
    {
        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, "uuid.yml", false);
        config.load();

        final List<String> uuids = new ArrayList<String>();

        for (String playerName : UUID_CACHE.keySet())
        {
            final UUID uuid = UUID_CACHE.get(playerName);

            if (uuid.toString().startsWith("deadbeef"))
            {
                continue;
            }

            uuids.add(playerName + ":" + uuid);
        }

        config.set("cache", uuids);
        config.save();
    }

    public static int purge()
    {
        final int size = UUID_CACHE.size();
        UUID_CACHE.clear();
        save();
        return size;
    }

    public static boolean isUniqueId(String uuid)
    {
        try
        {
            UUID.fromString(uuid);
        }
        catch (IllegalArgumentException ex)
        {
            return false;
        }

        return true;
    }

    public static UUID getUniqueId(OfflinePlayer offlinePlayer)
    {
        if (offlinePlayer.isOnline())
        {
            return TFM_PlayerData.getPlayerData(offlinePlayer.getPlayer()).getUniqueId();
        }

        return getUniqueId(offlinePlayer.getName());
    }

    public static UUID getUniqueId(String playerName)
    {
        if (UUID_CACHE.containsKey(playerName.toLowerCase()))
        {
            return UUID_CACHE.get(playerName.toLowerCase());
        }

        UUID uuid = TFM_UuidResolver.getUUIDOf(playerName);

        if (uuid == null)
        {
            uuid = generateSpoofUuid(playerName);
        }

        UUID_CACHE.put(playerName, uuid);

        save();

        return uuid;
    }

    private static UUID generateSpoofUuid(String name)
    {
        TFM_Log.info("Generating spoof UUID for " + name);
        name = name.toLowerCase();
        try
        {
            final MessageDigest digest = MessageDigest.getInstance("SHA1");
            final byte[] result = digest.digest(name.getBytes());
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < result.length; i++)
            {
                builder.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }

            return UUID.fromString(
                    "deadbeef"
                    + "-" + builder.substring(8, 12)
                    + "-" + builder.substring(12, 16)
                    + "-" + builder.substring(16, 20)
                    + "-" + builder.substring(20, 32));
        }
        catch (NoSuchAlgorithmException ex)
        {
            TFM_Log.severe(ex);
        }

        return UUID.randomUUID();
    }
}
