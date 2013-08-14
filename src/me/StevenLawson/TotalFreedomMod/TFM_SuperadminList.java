package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import me.StevenLawson.TotalFreedomMod.Commands.Command_logs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.FileUtil;

public class TFM_SuperadminList
{
    private static Map<String, TFM_Superadmin> superadminList = new HashMap<String, TFM_Superadmin>();
    private static List<String> superadminNames = new ArrayList<String>();
    private static List<String> superadminIPs = new ArrayList<String>();
    private static List<String> seniorAdminNames = new ArrayList<String>();
    private static int clean_threshold_hours = 24 * 7; // 1 Week

    private TFM_SuperadminList()
    {
        throw new AssertionError();
    }

    public static List<String> getSuperadminIPs()
    {
        return superadminIPs;
    }

    public static List<String> getSuperadminNames()
    {
        return superadminNames;
    }

    public static void loadSuperadminList()
    {
        try
        {
            superadminList.clear();

            TFM_Util.createDefaultConfiguration(TotalFreedomMod.SUPERADMIN_FILE, TotalFreedomMod.plugin_file);
            FileConfiguration config = YamlConfiguration.loadConfiguration(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));

            clean_threshold_hours = config.getInt("clean_threshold_hours", clean_threshold_hours);

            if (config.isConfigurationSection("superadmins"))
            {
                ConfigurationSection section = config.getConfigurationSection("superadmins");

                for (String admin_name : section.getKeys(false))
                {
                    TFM_Superadmin superadmin = new TFM_Superadmin(admin_name, section.getConfigurationSection(admin_name));
                    superadminList.put(admin_name.toLowerCase(), superadmin);
                }
            }
            else
            {
                TFM_Log.warning("Missing superadmins section in superadmin.yml.");
            }

            updateIndexLists();
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static void backupSavedList()
    {
        File a = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE);
        File b = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE + ".bak");
        FileUtil.copy(a, b);
    }

    public static void updateIndexLists()
    {
        superadminNames.clear();
        superadminIPs.clear();
        seniorAdminNames.clear();

        Iterator<Entry<String, TFM_Superadmin>> it = superadminList.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, TFM_Superadmin> pair = it.next();

            String admin_name = pair.getKey().toLowerCase();
            TFM_Superadmin superadmin = pair.getValue();

            if (superadmin.isActivated())
            {
                superadminNames.add(admin_name);

                for (String ip : superadmin.getIps())
                {
                    superadminIPs.add(ip);
                }

                if (superadmin.isSeniorAdmin())
                {
                    seniorAdminNames.add(admin_name);

                    for (String console_alias : superadmin.getConsoleAliases())
                    {
                        seniorAdminNames.add(console_alias.toLowerCase());
                    }
                }
            }
        }

        superadminNames = TFM_Util.removeDuplicates(superadminNames);
        superadminIPs = TFM_Util.removeDuplicates(superadminIPs);
        seniorAdminNames = TFM_Util.removeDuplicates(seniorAdminNames);

        TFM_AdminWorld.getInstance().wipeSuperadminCache();
    }

    public static void saveSuperadminList()
    {
        try
        {
            updateIndexLists();

            YamlConfiguration config = new YamlConfiguration();

            config.set("clean_threshold_hours", clean_threshold_hours);

            Iterator<Entry<String, TFM_Superadmin>> it = superadminList.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<String, TFM_Superadmin> pair = it.next();

                String admin_name = pair.getKey().toLowerCase();
                TFM_Superadmin superadmin = pair.getValue();

                config.set("superadmins." + admin_name + ".ips", TFM_Util.removeDuplicates(superadmin.getIps()));
                config.set("superadmins." + admin_name + ".last_login", TFM_Util.dateToString(superadmin.getLastLogin()));
                config.set("superadmins." + admin_name + ".custom_login_message", superadmin.getCustomLoginMessage());
                config.set("superadmins." + admin_name + ".is_senior_admin", superadmin.isSeniorAdmin());
                config.set("superadmins." + admin_name + ".console_aliases", TFM_Util.removeDuplicates(superadmin.getConsoleAliases()));
                config.set("superadmins." + admin_name + ".is_activated", superadmin.isActivated());
            }

            config.save(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static TFM_Superadmin getAdminEntry(String admin_name)
    {
        admin_name = admin_name.toLowerCase();

        if (superadminList.containsKey(admin_name))
        {
            return superadminList.get(admin_name);
        }
        else
        {
            return null;
        }
    }

    public static TFM_Superadmin getAdminEntry(Player player)
    {
        return getAdminEntry(player.getName().toLowerCase());
    }

    public static TFM_Superadmin getAdminEntryByIP(String ip)
    {
        Iterator<Entry<String, TFM_Superadmin>> it = superadminList.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, TFM_Superadmin> pair = it.next();
            TFM_Superadmin superadmin = pair.getValue();
            if (superadmin.getIps().contains(ip))
            {
                return superadmin;
            }
        }
        return null;
    }

    public static void updateLastLogin(Player player)
    {
        TFM_Superadmin admin_entry = getAdminEntry(player);
        if (admin_entry != null)
        {
            admin_entry.setLastLogin(new Date());
            saveSuperadminList();
        }
    }

    public static boolean isSeniorAdmin(CommandSender user)
    {
        return isSeniorAdmin(user, false);
    }

    public static boolean isSeniorAdmin(CommandSender user, boolean verify_is_superadmin)
    {
        if (verify_is_superadmin)
        {
            if (!isUserSuperadmin(user))
            {
                return false;
            }
        }

        String user_name = user.getName().toLowerCase();

        if (!(user instanceof Player))
        {
            return seniorAdminNames.contains(user_name);
        }

        TFM_Superadmin admin_entry = getAdminEntry((Player) user);
        if (admin_entry != null)
        {
            return admin_entry.isSeniorAdmin();
        }

        return false;
    }

    public static boolean isUserSuperadmin(CommandSender user)
    {
        if (!(user instanceof Player))
        {
            return true;
        }

        if (Bukkit.getOnlineMode())
        {
            if (superadminNames.contains(user.getName().toLowerCase()))
            {
                return true;
            }
        }

        try
        {
            String user_ip = ((Player) user).getAddress().getAddress().getHostAddress();
            if (user_ip != null && !user_ip.isEmpty())
            {
                if (superadminIPs.contains(user_ip))
                {
                    return true;
                }
            }
        }
        catch (Exception ex)
        {
            return false;
        }

        return false;
    }

    public static boolean checkPartialSuperadminIP(String user_ip, String user_name)
    {
        try
        {
            user_ip = user_ip.trim();

            if (superadminIPs.contains(user_ip))
            {
                return true;
            }
            else
            {
                String match_ip = null;
                for (String test_ip : getSuperadminIPs())
                {
                    if (TFM_Util.fuzzyIpMatch(user_ip, test_ip, 3))
                    {
                        match_ip = test_ip;
                        break;
                    }
                }

                if (match_ip != null)
                {
                    TFM_Superadmin admin_entry = getAdminEntryByIP(match_ip);

                    if (admin_entry != null)
                    {
                        if (admin_entry.getName().equalsIgnoreCase(user_name))
                        {
                            List<String> ips = admin_entry.getIps();
                            ips.add(user_ip);
                            admin_entry.setIps(ips);
                            saveSuperadminList();
                        }
                    }

                    return true;
                }
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }

        return false;
    }

    public static boolean isSuperadminImpostor(CommandSender user)
    {
        if (!(user instanceof Player))
        {
            return false;
        }

        Player player = (Player) user;

        if (superadminNames.contains(player.getName().toLowerCase()))
        {
            return !isUserSuperadmin(player);
        }

        return false;
    }

    public static void addSuperadmin(String admin_name, List<String> ips)
    {
        try
        {
            admin_name = admin_name.toLowerCase();

            if (superadminList.containsKey(admin_name))
            {
                TFM_Superadmin superadmin = superadminList.get(admin_name);
                superadmin.setActivated(true);
                superadmin.getIps().addAll(ips);
                superadmin.setLastLogin(new Date());
            }
            else
            {
                Date last_login = new Date();
                String custom_login_message = "";
                boolean is_senior_admin = false;
                List<String> console_aliases = new ArrayList<String>();

                TFM_Superadmin superadmin = new TFM_Superadmin(admin_name, ips, last_login, custom_login_message, is_senior_admin, console_aliases, true);
                superadminList.put(admin_name.toLowerCase(), superadmin);
            }

            saveSuperadminList();
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static void addSuperadmin(Player player)
    {
        String admin_name = player.getName().toLowerCase();
        List<String> ips = Arrays.asList(player.getAddress().getAddress().getHostAddress());

        addSuperadmin(admin_name, ips);
    }

    public static void addSuperadmin(String admin_name)
    {
        addSuperadmin(admin_name, new ArrayList<String>());
    }

    public static void removeSuperadmin(String admin_name)
    {
        try
        {
            admin_name = admin_name.toLowerCase();

            if (superadminList.containsKey(admin_name))
            {
                TFM_Superadmin superadmin = superadminList.get(admin_name);
                superadmin.setActivated(false);
                Command_logs.deactivateSuperadmin(superadmin);
                saveSuperadminList();
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static void removeSuperadmin(Player player)
    {
        removeSuperadmin(player.getName());
    }

    public static void cleanSuperadminList(boolean verbose)
    {
        try
        {
            Iterator<Entry<String, TFM_Superadmin>> it = superadminList.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<String, TFM_Superadmin> pair = it.next();
                TFM_Superadmin superadmin = pair.getValue();
                if (superadmin.isActivated() && !superadmin.isSeniorAdmin())
                {
                    Date last_login = superadmin.getLastLogin();

                    long hours_since_login = TimeUnit.HOURS.convert(new Date().getTime() - last_login.getTime(), TimeUnit.MILLISECONDS);

                    if (hours_since_login > clean_threshold_hours)
                    {
                        if (verbose)
                        {
                            TFM_Util.adminAction("TotalFreedomSystem", "Deactivating superadmin \"" + superadmin.getName() + "\", inactive for " + hours_since_login + " hours.", true);
                        }

                        superadmin.setActivated(false);
                        Command_logs.deactivateSuperadmin(superadmin);
                    }
                }
            }
            saveSuperadminList();
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static boolean verifyIdentity(String admin_name, String ip) throws Exception
    {
        if (Bukkit.getOnlineMode())
        {
            return true;
        }

        TFM_Superadmin admin_entry = getAdminEntry(admin_name);
        if (admin_entry != null)
        {
            return admin_entry.getIps().contains(ip);
        }
        else
        {
            throw new Exception();
        }
    }
}
