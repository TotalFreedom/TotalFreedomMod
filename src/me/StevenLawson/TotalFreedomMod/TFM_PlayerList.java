package me.StevenLawson.TotalFreedomMod;

import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TFM_PlayerList
{
    private static final TFM_PlayerList INSTANCE;
    private final Map<UUID, TFM_PlayerEntry> playerList;
    private TFM_Config config;

    static
    {
        INSTANCE = new TFM_PlayerList();
    }

    private TFM_PlayerList()
    {
        this.playerList = new HashMap<UUID, TFM_PlayerEntry>();
    }

    public TFM_Config getConfig()
    {
        return config;
    }

    public void load()
    {
        playerList.clear();

        if (config == null)
        {
            config = new TFM_Config(TotalFreedomMod.plugin, "playerlist.yml", false);
        }

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

            final TFM_PlayerEntry entry = new TFM_PlayerEntry(uuid, config.getConfigurationSection(uuidString));

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

        TFM_Log.info("Loaded playerdata for " + playerList.size() + " players.");
    }

    private void saveAll()
    {
        // Put entries
        for (TFM_PlayerEntry entry : playerList.values())
        {
            entry.save();
        }
    }

    public TFM_PlayerEntry getEntry(String player)
    {

        for (TFM_PlayerEntry entry : playerList.values())
        {
            if (entry.getLastJoinName().equalsIgnoreCase(player))
            {
                return entry;
            }
        }

        return null;
    }

    public TFM_PlayerEntry getEntry(UUID uuid)
    {
        return playerList.get(uuid);
    }

    public boolean existsEntry(Player player)
    {
        return playerList.containsKey(player.getUniqueId());
    }

    public TFM_PlayerEntry getEntry(Player player)
    {
        final UUID uuid = player.getUniqueId();

        if (existsEntry(player))
        {
            return playerList.get(uuid);
        }

        final TFM_PlayerEntry entry = new TFM_PlayerEntry(uuid);

        entry.setFirstJoinName(player.getName());
        entry.setLastJoinName(player.getName());

        final long unix = TFM_Util.getUnixTime();
        entry.setFirstJoinUnix(unix);
        entry.setLastJoinUnix(unix);

        entry.addIp(TFM_Util.getIp(player));

        entry.save();
        playerList.put(uuid, entry);

        return entry;
    }

    public void purgeAll()
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

    public static TFM_PlayerList getInstance()
    {
        return INSTANCE;
    }
}
