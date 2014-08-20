package me.StevenLawson.TotalFreedomMod;

import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.util.com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TFM_PlayerList
{
    private static final Map<UUID, TFM_Player> playerList;
    private final static TFM_Config config;

    static
    {
        playerList = new HashMap<UUID, TFM_Player>();
        config = new TFM_Config(TotalFreedomMod.plugin, "playerlist.yml", false);
    }

    private TFM_PlayerList()
    {
        throw new AssertionError();
    }

    public static Set<TFM_Player> getAllPlayers()
    {
        return Sets.newHashSet(playerList.values());
    }

    public static TFM_Config getConfig()
    {
        return config;
    }

    public static void load()
    {
        TFM_Util.TFMethodTimer timer = new TFM_Util.TFMethodTimer();
        timer.start();

        playerList.clear();
        config.load();

        // Load players from config
        for (String uuidString : config.getKeys(false))
        {
            if (!TFM_Util.isUniqueId(uuidString))
            {
                TFM_Log.warning("Invalid playerlist UUID: " + uuidString + ", Skipping...");
                continue;
            }

            final UUID uuid = UUID.fromString(uuidString);

            final TFM_Player entry = new TFM_Player(uuid, config.getConfigurationSection(uuidString));

            if (!entry.isComplete())
            {
                TFM_Log.warning("Incomplete playerlist entry: " + uuidString + ", Skipping...");
                continue;
            }

            playerList.put(uuid, entry);
        }

        // Load online players
        for (Player player : Bukkit.getOnlinePlayers())
        {
            getEntry(player);
        }

        // Save list
        saveAll();

        timer.update();

        TFM_Log.info("Loaded playerdata for " + playerList.size() + " players in " + timer.getTotal() + " ms.");
    }

    private static void saveAll()
    {
        // Put entries
        for (TFM_Player entry : playerList.values())
        {
            entry.save(false);
        }

        getConfig().save();
    }

    public static TFM_Player getEntry(String player)
    {

        for (TFM_Player entry : playerList.values())
        {
            if (entry.getLastLoginName().equalsIgnoreCase(player))
            {
                return entry;
            }
        }

        return null;
    }

    public static TFM_Player getEntry(UUID uuid)
    {
        return playerList.get(uuid);
    }

    public static boolean existsEntry(Player player)
    {
        return playerList.containsKey(TFM_Util.getUuid(player));
    }

    public static TFM_Player getEntry(Player player)
    {
        final UUID uuid = TFM_Util.getUuid(player);

        if (existsEntry(player))
        {
            return playerList.get(uuid);
        }

        final TFM_Player entry = new TFM_Player(uuid);

        entry.setFirstLoginName(player.getName());
        entry.setLastLoginName(player.getName());

        final long unix = TFM_Util.getUnixTime();
        entry.setFirstLoginUnix(unix);
        entry.setLastLoginUnix(unix);

        entry.addIp(TFM_Util.getIp(player));

        entry.save();
        playerList.put(uuid, entry);

        return entry;
    }

    public static void setUuid(TFM_Player player, UUID oldUuid, UUID newUuid)
    {
        if (!playerList.containsKey(oldUuid))
        {
            TFM_Log.warning("Could not set new UUID for player " + player.getLastLoginName() + ", player is not loaded!");
            return;
        }

        if (oldUuid.equals(newUuid))
        {
            TFM_Log.warning("could not set new UUID for player " + player.getLastLoginName() + ", UUIDs match.");
            return;
        }

        final TFM_Player newPlayer = new TFM_Player(
                newUuid,
                player.getFirstLoginName(),
                player.getLastLoginName(),
                player.getFirstLoginUnix(),
                player.getLastLoginUnix(),
                player.getIps());

        playerList.remove(oldUuid);
        playerList.put(newUuid, newPlayer);

        final TFM_Config config = getConfig();
        config.set(oldUuid.toString(), null);
        config.save();

        newPlayer.save();
    }

    public static void purgeAll()
    {
        // Clear the config entries
        for (String key : config.getKeys(false))
        {
            config.set(key, null);
        }

        config.save();

        // Load online players
        load();
    }
}
