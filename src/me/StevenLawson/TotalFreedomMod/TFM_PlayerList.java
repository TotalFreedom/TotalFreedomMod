package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TFM_PlayerList
{
    private static final TFM_PlayerList INSTANCE = new TFM_PlayerList();
    private static final String USERLIST_FILENAME = "playerlist.yml";
    private final Map<UUID, PlayerEntry> playerList;
    private final YamlConfiguration config;
    private File configFile;

    private TFM_PlayerList()
    {
        this.playerList = new HashMap<UUID, PlayerEntry>();
        this.config = new YamlConfiguration();
    }

    public File getConfigFile()
    {
        return new File(TotalFreedomMod.plugin.getDataFolder(), USERLIST_FILENAME);
    }

    public void load()
    {
        playerList.clear();

        configFile = new File(TotalFreedomMod.plugin.getDataFolder(), USERLIST_FILENAME);

        if (configFile.exists())
        {
            try
            {
                config.load(configFile);
            }
            catch (Exception ex)
            {
                TFM_Log.warning("Could not load player config file: " + ex.getMessage());
                TFM_Log.warning("Purging...");
                purgeAll();
                return;
            }

            // Load players from config
            for (String uuidString : config.getKeys(false))
            {
                final UUID uuid;
                try
                {
                    uuid = UUID.fromString(uuidString);
                }
                catch (IllegalArgumentException ex)
                {
                    TFM_Log.warning("Invalid playerlist UUID: " + uuidString + ", Skipping...");
                    continue;
                }

                final PlayerEntry entry = new PlayerEntry(uuid, config.getConfigurationSection(uuidString));

                if (!entry.isComplete())
                {
                    TFM_Log.warning("Incomplete playerlist entry: " + uuidString + ", Skipping...");
                    continue;
                }

                playerList.put(uuid, entry);
            }
        }

        // Load online players
        for (Player player : Bukkit.getOnlinePlayers())
        {
            getEntry(player);
        }

        // Save list
        saveAll();
    }

    private void saveAll()
    {
        // Put entries
        for (PlayerEntry entry : playerList.values())
        {
            entry.save();
        }
    }

    public PlayerEntry getEntry(String player)
    {

        for (PlayerEntry entry : playerList.values())
        {
            if (entry.getLastJoinName().equalsIgnoreCase(player))
            {
                return entry;
            }
        }

        return null;
    }

    public PlayerEntry getEntry(Player player)
    {
        final UUID uuid = player.getUniqueId();

        PlayerEntry entry = playerList.get(uuid);

        if (entry == null)
        {
            entry = new PlayerEntry(uuid);

            entry.setFirstJoinName(player.getName());
            entry.setLastJoinName(player.getName());

            final long unix = TFM_Util.getUnixTime();
            entry.setFirstJoinUnix(unix);
            entry.setLastJoinUnix(unix);

            entry.save();
            playerList.put(uuid, entry);
        }

        return entry;
    }

    public void purgeAll()
    {
        // Clear the config entries
        for (String key : config.getKeys(false))
        {
            config.set(key, null);
        }

        // Save the config
        try
        {
            config.save(configFile);
        }
        catch (IOException ex)
        {
            TFM_Log.severe("Could not purge config file: " + ex.getMessage());
            TFM_Log.severe(ex);
        }

        // Load online players
        load();
    }

    /*public String searchByPartialName(String needle)
     {
     needle = needle.toLowerCase().trim();

     Integer minEditDistance = null;
     String minEditMatch = null;
     Iterator<UUID> it = playerList.keySet().iterator();
     while (it.hasNext())
     {
     String haystack = it.next();
     int editDistance = StringUtils.getLevenshteinDistance(needle, haystack.toLowerCase());
     if (minEditDistance == null || minEditDistance.intValue() > editDistance)
     {
     minEditDistance = editDistance;
     minEditMatch = haystack;
     }
     }
     return minEditMatch;
     }*/
    public static TFM_PlayerList getInstance()
    {
        return INSTANCE;
    }

    public final class PlayerEntry
    {
        private final UUID uuid;
        private String firstJoinName;
        private String lastJoinName;
        private long firstJoinUnix;
        private long lastJoinUnix;
        private final List<String> ips;

        protected PlayerEntry(UUID uuid, ConfigurationSection section)
        {
            this(uuid);

            this.firstJoinName = section.getString("firstjoinname");
            this.lastJoinName = section.getString("lastjoinname");

            this.firstJoinUnix = section.getLong("firstjoinunix");
            this.firstJoinUnix = section.getLong("lastjoinunix");

            this.ips.addAll(section.getStringList("ips"));
        }

        protected PlayerEntry(UUID uuid)
        {
            this.uuid = uuid;
            this.ips = new ArrayList<String>();
        }

        // Getters / Setters below
        public UUID getUniqueId()
        {
            return uuid;
        }

        public List<String> getIps()
        {
            return Collections.unmodifiableList(ips);
        }

        public String getFirstJoinName()
        {
            return firstJoinName;
        }

        public void setFirstJoinName(String firstJoinName)
        {
            this.firstJoinName = firstJoinName;
        }

        public String getLastJoinName()
        {
            return lastJoinName;
        }

        public void setLastJoinName(String lastJoinName)
        {
            this.lastJoinName = lastJoinName;
        }

        public long getFirstJoinUnix()
        {
            return firstJoinUnix;
        }

        public void setFirstJoinUnix(long firstJoinUnix)
        {
            this.firstJoinUnix = firstJoinUnix;
        }

        public long getLastJoinUnix()
        {
            return lastJoinUnix;
        }

        public void setLastJoinUnix(long lastJoinUnix)
        {
            this.lastJoinUnix = lastJoinUnix;
        }

        public boolean addIp(String ip)
        {
            if (!ips.contains(ip))
            {
                ips.add(ip);
                return true;
            }
            return false;
        }

        private boolean isComplete()
        {
            return firstJoinName != null
                    && lastJoinName != null
                    && firstJoinUnix != 0
                    && lastJoinUnix != 0
                    && !ips.isEmpty();
        }

        public void save()
        {
            if (!isComplete())
            {
                throw new IllegalStateException("Entry is not complete");
            }

            final ConfigurationSection section;

            if (config.isConfigurationSection(uuid.toString()))
            {
                section = config.getConfigurationSection(uuid.toString());
            }
            else
            {
                section = config.createSection(uuid.toString());
            }

            section.set("firstjoinname", firstJoinName);
            section.set("lastjoinname", lastJoinName);
            section.set("firstjoinunix", firstJoinUnix);
            section.set("lastjoinunix", lastJoinUnix);
            section.set("ips", ips);

            // Save config
            try
            {
                config.save(configFile);
            }
            catch (IOException ex)
            {
                TFM_Log.severe("Could not save player entry: " + uuid.toString() + " (" + lastJoinName + ")");
                TFM_Log.severe(ex);
            }
        }
    }
}
