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
    private static final Map<String, TFM_Superadmin> superadminList = new HashMap<String, TFM_Superadmin>();
    private static List<String> superadminNames = new ArrayList<String>();
    private static List<String> senioradminNames = new ArrayList<String>();
    private static List<String> telnetadminNames = new ArrayList<String>();
    private static List<String> superadminIPs = new ArrayList<String>();
    private static int cleanThreshold = 24 * 7; // 1 Week in hours

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

    public static List<String> getTelnetadminNames()
    {
        return telnetadminNames;
    }

    public static List<String> getSenioradminNames()
    {
        return senioradminNames;
    }

    public static void loadSuperadminList()
    {
        try
        {
            superadminList.clear();

            TFM_Util.createDefaultConfiguration(TotalFreedomMod.SUPERADMIN_FILE);
            FileConfiguration config = YamlConfiguration.loadConfiguration(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));

            cleanThreshold = config.getInt("clean_threshold_hours", cleanThreshold);

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
        telnetadminNames.clear();
        senioradminNames.clear();
        superadminIPs.clear();

        Iterator<Entry<String, TFM_Superadmin>> it = superadminList.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, TFM_Superadmin> pair = it.next();

            String name = pair.getKey().toLowerCase();
            TFM_Superadmin superadmin = pair.getValue();

            if (superadmin.isActivated())
            {
                superadminNames.add(name);

                for (String ip : superadmin.getIps())
                {
                    superadminIPs.add(ip);
                }

                if (superadmin.isSeniorAdmin())
                {
                    senioradminNames.add(name);

                    for (String console_alias : superadmin.getConsoleAliases())
                    {
                        senioradminNames.add(console_alias.toLowerCase());
                    }
                }

                if (superadmin.isTelnetAdmin())
                {
                    telnetadminNames.add(name);
                }
            }
        }

        superadminNames = TFM_Util.removeDuplicates(superadminNames);
        telnetadminNames = TFM_Util.removeDuplicates(telnetadminNames);
        senioradminNames = TFM_Util.removeDuplicates(senioradminNames);
        superadminIPs = TFM_Util.removeDuplicates(superadminIPs);

        TFM_AdminWorld.getInstance().wipeAccessCache();
    }

    public static void saveSuperadminList()
    {
        try
        {
            updateIndexLists();

            YamlConfiguration config = new YamlConfiguration();

            config.set("clean_threshold_hours", cleanThreshold);

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
                config.set("superadmins." + admin_name + ".is_telnet_admin", superadmin.isTelnetAdmin());
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

    public static TFM_Superadmin getAdminEntry(Player player)
    {
        final String name = player.getName().toLowerCase();

        if (Bukkit.getOnlineMode())
        {
            if (superadminList.containsKey(name))
            {
                return superadminList.get(name);
            }
        }

        try
        {
            final String ip = player.getAddress().getAddress().getHostAddress().trim();
            if (ip != null && !ip.isEmpty())
            {
                return getAdminEntryByIP(ip);
            }
        }
        catch (Exception ex)
        {
            return null;
        }
        return null;
    }

    @Deprecated
    public static TFM_Superadmin getAdminEntry(String name)
    {
        name = name.toLowerCase();

        if (superadminList.containsKey(name))
        {
            return superadminList.get(name);
        }
        else
        {
            return null;
        }
    }

    public static TFM_Superadmin getAdminEntryByIP(String ip)
    {
        return getAdminEntryByIP(ip, false);
    }

    public static TFM_Superadmin getAdminEntryByIP(String needleIP, boolean fuzzy)
    {
        Iterator<Entry<String, TFM_Superadmin>> it = superadminList.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, TFM_Superadmin> pair = it.next();
            TFM_Superadmin superadmin = pair.getValue();
            if (fuzzy)
            {
                for (String haystackIP : superadmin.getIps())
                {
                    if (TFM_Util.fuzzyIpMatch(needleIP, haystackIP, 3))
                    {
                        return superadmin;
                    }
                }
            }
            else
            {
                if (superadmin.getIps().contains(needleIP))
                {
                    return superadmin;
                }
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

    public static boolean isSeniorAdmin(CommandSender user, boolean verifySuperadmin)
    {
        if (verifySuperadmin)
        {
            if (!isUserSuperadmin(user))
            {
                return false;
            }
        }

        String username = user.getName().toLowerCase();

        if (!(user instanceof Player))
        {
            return senioradminNames.contains(username);
        }

        TFM_Superadmin entry = getAdminEntry((Player) user);
        if (entry != null)
        {
            return entry.isSeniorAdmin();
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
            String ip = ((Player) user).getAddress().getAddress().getHostAddress();
            if (ip != null && !ip.isEmpty())
            {
                if (superadminIPs.contains(ip))
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

    public static boolean checkPartialSuperadminIP(String ip, String name)
    {
        try
        {
            ip = ip.trim();

            if (superadminIPs.contains(ip))
            {
                return true;
            }
            else
            {
                String matchIp = null;
                for (String testIp : getSuperadminIPs())
                {
                    if (TFM_Util.fuzzyIpMatch(ip, testIp, 3))
                    {
                        matchIp = testIp;
                        break;
                    }
                }

                if (matchIp != null)
                {
                    TFM_Superadmin entry = getAdminEntryByIP(matchIp);

                    if (entry != null)
                    {
                        if (entry.getName().equalsIgnoreCase(name))
                        {
                            List<String> ips = entry.getIps();
                            ips.add(ip);
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

    public static void addSuperadmin(String username, List<String> ips)
    {
        try
        {
            username = username.toLowerCase();

            if (superadminList.containsKey(username))
            {
                TFM_Superadmin superadmin = superadminList.get(username);
                superadmin.setActivated(true);
                superadmin.getIps().addAll(ips);
                superadmin.setLastLogin(new Date());
            }
            else
            {
                TFM_Superadmin superadmin = new TFM_Superadmin(username, ips, new Date(), "", false, false, new ArrayList<String>(), true);
                superadminList.put(username.toLowerCase(), superadmin);
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
        String username = player.getName().toLowerCase();
        List<String> ips = Arrays.asList(player.getAddress().getAddress().getHostAddress());

        addSuperadmin(username, ips);
    }

    public static void addSuperadmin(String adminName)
    {
        addSuperadmin(adminName, new ArrayList<String>());
    }

    public static void removeSuperadmin(String username)
    {
        try
        {
            username = username.toLowerCase();

            if (superadminList.containsKey(username))
            {
                TFM_Superadmin superadmin = superadminList.get(username);
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
                    Date lastLogin = superadmin.getLastLogin();

                    long lastLoginHours = TimeUnit.HOURS.convert(new Date().getTime() - lastLogin.getTime(), TimeUnit.MILLISECONDS);

                    if (lastLoginHours > cleanThreshold)
                    {
                        if (verbose)
                        {
                            TFM_Util.adminAction("TotalFreedomSystem", "Deactivating superadmin \"" + superadmin.getName() + "\", inactive for " + lastLoginHours + " hours.", true);
                        }

                        superadmin.setActivated(false);
                        Command_logs.deactivateSuperadmin(superadmin);
                        TFM_TwitterHandler.getInstance().delTwitter(superadmin.getName());
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

    public static boolean verifyIdentity(String username, String ip) throws Exception
    {
        if (Bukkit.getOnlineMode())
        {
            return true;
        }

        TFM_Superadmin entry = getAdminEntry(username);
        if (entry != null)
        {
            return entry.getIps().contains(ip);
        }
        else
        {
            throw new Exception();
        }
    }
}
