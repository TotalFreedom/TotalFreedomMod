package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TFM_UserList
{
    private static final String USERLIST_FILENAME = "userlist.yml";
    private static TFM_UserList instance = null;
    private Map<String, TFM_UserListEntry> userlist = new HashMap<String, TFM_UserListEntry>();
    private final TotalFreedomMod plugin;

    protected TFM_UserList(TotalFreedomMod plugin)
    {
        this.plugin = plugin;

        primeList();
    }

    private void primeList()
    {
        try
        {
            userlist.clear();

            FileConfiguration savedUserlist = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), USERLIST_FILENAME));

            for (String username : savedUserlist.getKeys(false))
            {
                TFM_UserListEntry entry = new TFM_UserListEntry(username, savedUserlist.getStringList(username));
                userlist.put(username, entry);
            }

            for (Player player : plugin.getServer().getOnlinePlayers())
            {
                addUser(player);
            }

            exportList();
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Error loading Userlist, resetting list: " + ex.getMessage());
            purge();
        }
    }

    private void exportList()
    {
        FileConfiguration newUserlist = new YamlConfiguration();

        for (TFM_UserListEntry entry : userlist.values())
        {
            newUserlist.set(entry.getUsername(), entry.getIpAddresses());
        }

        try
        {
            newUserlist.save(new File(plugin.getDataFolder(), USERLIST_FILENAME));
        }
        catch (IOException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static TFM_UserList getInstance(TotalFreedomMod plugin)
    {
        if (instance == null)
        {
            instance = new TFM_UserList(plugin);
        }
        return instance;
    }

    public void addUser(Player player)
    {
        addUser(player.getName(), player.getAddress().getAddress().getHostAddress());
    }

    public void addUser(String username, String ip)
    {
        username = username.toLowerCase();

        TFM_UserListEntry entry = userlist.get(username);
        if (entry == null)
        {
            entry = new TFM_UserListEntry(username);
        }

        userlist.put(username, entry);

        if (entry.addIpAddress(ip))
        {
            exportList();
        }
    }

    public TFM_UserListEntry getEntry(Player player)
    {
        return getEntry(player.getName());
    }

    public TFM_UserListEntry getEntry(String username)
    {
        return userlist.get(username.toLowerCase());
    }

    public void purge()
    {
        userlist.clear();

        for (Player player : plugin.getServer().getOnlinePlayers())
        {
            addUser(player);
        }

        exportList();
    }

    public String searchByPartialName(String needle)
    {
        needle = needle.toLowerCase().trim();
        Integer minEditDistance = null;
        String minEditMatch = null;
        Iterator<String> it = userlist.keySet().iterator();
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
    }

    public class TFM_UserListEntry
    {
        private String username;
        private List<String> ipAddresses = new ArrayList<String>();

        public TFM_UserListEntry(String username, List<String> ipAddresses)
        {
            this.username = username;
            this.ipAddresses = ipAddresses;
        }

        public TFM_UserListEntry(String username)
        {
            this.username = username;
        }

        public List<String> getIpAddresses()
        {
            return ipAddresses;
        }

        public String getUsername()
        {
            return username;
        }

        public boolean addIpAddress(String ip)
        {
            if (!ipAddresses.contains(ip))
            {
                ipAddresses.add(ip);
                return true;
            }
            return false;
        }
    }
}
