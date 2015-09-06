package me.StevenLawson.TotalFreedomMod;

import com.google.common.collect.Sets;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TFM_PlayerList
{

    private static final Map<UUID, TFM_Player> PLAYER_LIST = new HashMap<UUID, TFM_Player>();

    private TFM_PlayerList()
    {
        throw new AssertionError();
    }

    public static Set<TFM_Player> getAllPlayers()
    {
        return Collections.unmodifiableSet(Sets.newHashSet(PLAYER_LIST.values()));
    }

    public static void load()
    {
        PLAYER_LIST.clear();

        // Load online players
        for (Player player : Bukkit.getOnlinePlayers())
        {
            getEntry(player);
        }

        TFM_Log.info("Loaded playerdata for " + PLAYER_LIST.size() + " players");
    }

    public static void saveAll()
    {
        for (TFM_Player entry : PLAYER_LIST.values())
        {
            save(entry);
        }
    }

    // May return null
    public static TFM_Player getEntry(UUID uuid)
    {
        if (PLAYER_LIST.containsKey(uuid))
        {
            return PLAYER_LIST.get(uuid);
        }

        final File configFile = getConfigFile(uuid);

        if (!configFile.exists())
        {
            return null;
        }

        final TFM_Player entry = new TFM_Player(uuid, getConfig(uuid));

        if (entry.isComplete())
        {
            PLAYER_LIST.put(uuid, entry);
            return entry;
        }
        else
        {
            TFM_Log.warning("Could not load entry: Entry is not complete!");
            configFile.delete();
        }

        return null;
    }

    public static TFM_Player getEntry(Player player)
    {
        final UUID uuid = TFM_UuidManager.getUniqueId(player);
        TFM_Player entry = getEntry(uuid);

        if (entry != null)
        {
            return entry;
        }

        final long unix = TFM_Util.getUnixTime();
        entry = new TFM_Player(uuid);
        entry.setFirstLoginName(player.getName());
        entry.setLastLoginName(player.getName());
        entry.setFirstLoginUnix(unix);
        entry.setLastLoginUnix(unix);
        entry.addIp(TFM_Util.getIp(player));

        save(entry);
        PLAYER_LIST.put(uuid, entry);

        return entry;
    }

    public static void removeEntry(Player player)
    {
        final UUID uuid = TFM_UuidManager.getUniqueId(player);

        if (!PLAYER_LIST.containsKey(uuid))
        {
            return;
        }

        save(PLAYER_LIST.get(uuid));

        PLAYER_LIST.remove(uuid);
    }

    public static boolean existsEntry(Player player)
    {
        return existsEntry(TFM_UuidManager.getUniqueId(player));
    }

    public static boolean existsEntry(UUID uuid)
    {
        return getConfigFile(uuid).exists();
    }

    public static void setUniqueId(TFM_Player entry, UUID newUuid)
    {
        if (entry.getUniqueId().equals(newUuid))
        {
            TFM_Log.warning("Not setting new UUID: UUIDs match!");
            return;
        }

        // Add new entry
        final TFM_Player newEntry = new TFM_Player(
                newUuid,
                entry.getFirstLoginName(),
                entry.getLastLoginName(),
                entry.getFirstLoginUnix(),
                entry.getLastLoginUnix(),
                entry.getIps());
        newEntry.save();
        PLAYER_LIST.put(newUuid, newEntry);

        // Remove old entry
        PLAYER_LIST.remove(entry.getUniqueId());
        final File oldFile = getConfigFile(entry.getUniqueId());
        if (oldFile.exists() && !oldFile.delete())
        {
            TFM_Log.warning("Could not delete config: " + getConfigFile(entry.getUniqueId()).getName());
        }
    }

    public static void purgeAll()
    {
        for (File file : getConfigFolder().listFiles())
        {
            file.delete();
        }

        // Load online players
        load();
    }

    public static File getConfigFolder()
    {
        return new File(TotalFreedomMod.plugin.getDataFolder(), "players");
    }

    public static File getConfigFile(UUID uuid)
    {
        return new File(getConfigFolder(), uuid + ".yml");
    }

    public static TFM_Config getConfig(UUID uuid)
    {
        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, getConfigFile(uuid), false);
        config.load();
        return config;
    }

    public static void save(TFM_Player entry)
    {
        if (!entry.isComplete())
        {
            throw new IllegalArgumentException("Entry is not complete!");
        }

        final TFM_Config config = getConfig(entry.getUniqueId());
        config.set("firstjoinname", entry.getFirstLoginName());
        config.set("lastjoinname", entry.getLastLoginName());
        config.set("firstjoinunix", entry.getFirstLoginUnix());
        config.set("lastjoinunix", entry.getLastLoginUnix());
        config.set("ips", entry.getIps());
        config.save();
    }
}
