package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.util.Collections;
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

    private TFM_PlayerList()
    {
        throw new AssertionError();
    }

    public static Set<TFM_Player> getAllPlayers()
    {
        return Collections.unmodifiableSet(Sets.newHashSet(playerList.values()));
    }

    public static void load()
    {
        final TFM_Util.MethodTimer timer = new TFM_Util.MethodTimer();
        timer.start();

        playerList.clear();

        // Load online players
        for (Player player : Bukkit.getOnlinePlayers())
        {
            getEntry(player);
        }

        timer.update();

        TFM_Log.info("Loaded playerdata for " + playerList.size() + " players in " + timer.getTotal() + " ms.");
    }

    public static void saveAll()
    {
        for (TFM_Player entry : playerList.values())
        {
            save(entry);
        }
    }

    @Deprecated
    private static TFM_Player getEntry(String player)
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

    // May return null
    public static TFM_Player getEntry(UUID uuid)
    {
        if (playerList.containsKey(uuid))
        {
            return playerList.get(uuid);
        }

        final File configFile = getConfigFile(uuid);

        if (!configFile.exists())
        {
            return null;
        }

        final TFM_Player entry = new TFM_Player(uuid, getConfig(uuid));

        if (entry.isComplete())
        {
            playerList.put(uuid, entry);
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
        final UUID uuid = TFM_Util.getUniqueId(player);
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
        playerList.put(uuid, entry);

        return entry;
    }

    public static boolean existsEntry(Player player)
    {
        return existsEntry(TFM_Util.getUniqueId(player));
    }

    public static boolean existsEntry(UUID uuid)
    {
        return getConfigFile(uuid).exists();
    }

    public static void setUniqueId(TFM_Player entry, UUID newUuid)
    {
        if (entry.getUniqueId().equals(newUuid))
        {
            throw new IllegalArgumentException("Cannot set new UUID: UUIDs match");
        }

        final boolean reAdd = playerList.containsKey(entry.getUniqueId());
        playerList.remove(entry.getUniqueId());

        final TFM_Player newPlayer = new TFM_Player(
                newUuid,
                entry.getFirstLoginName(),
                entry.getLastLoginName(),
                entry.getFirstLoginUnix(),
                entry.getLastLoginUnix(),
                entry.getIps());

        if (reAdd)
        {
            playerList.put(newUuid, newPlayer);
        }

        newPlayer.save();

        if (!getConfigFile(entry.getUniqueId()).delete())
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
