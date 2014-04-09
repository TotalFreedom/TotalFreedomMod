package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.StevenLawson.TotalFreedomMod.Commands.Command_logs;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.FileUtil;

public class TFM_AdminList
{
    private static final Map<UUID, TFM_Admin> adminList;
    private static final Set<UUID> superadminUUIDs;
    private static final Set<UUID> telnetadminUUIDs;
    private static final Set<UUID> senioradminUUIDs;
    private static final Set<String> senioradminAliases;
    private static final Set<String> superadminIps;
    private static int cleanThreshold = 24 * 7; // 1 Week in hours

    static
    {
        adminList = new HashMap<UUID, TFM_Admin>();
        superadminUUIDs = new HashSet<UUID>();
        telnetadminUUIDs = new HashSet<UUID>();
        senioradminUUIDs = new HashSet<UUID>();
        senioradminAliases = new HashSet<String>();
        superadminIps = new HashSet<String>();
    }

    private TFM_AdminList()
    {
        throw new AssertionError();
    }

    public static Set<UUID> getSuperadminUUIDs()
    {
        return Collections.unmodifiableSet(superadminUUIDs);
    }

    public static Set<UUID> getTelnetadminUUIDs()
    {
        return Collections.unmodifiableSet(telnetadminUUIDs);
    }

    public static Set<UUID> getSenioradminUUIDs()
    {
        return Collections.unmodifiableSet(senioradminUUIDs);
    }

    public static Set<String> getConsoleAliases()
    {
        return Collections.unmodifiableSet(senioradminAliases);
    }

    public static Set<String> getSuperadminIps()
    {
        return Collections.unmodifiableSet(superadminIps);
    }

    public File getConfigFile()
    {
        return new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE);
    }

    public static void loadSuperadminList()
    {
        try
        {
            adminList.clear();

            TFM_Util.createDefaultConfiguration(TotalFreedomMod.SUPERADMIN_FILE);

            final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));

            cleanThreshold = config.getInt("clean_threshold_hours", cleanThreshold);

            if (config.isConfigurationSection("superadmins"))
            {
                // TODO: Loading from old
            }

            if (!config.isConfigurationSection("admins"))
            {
                TFM_Log.warning("Missing admins section in superadmin.yml.");
                return;
            }

            final ConfigurationSection section = config.getConfigurationSection("superadmins");

            for (String uuidString : section.getKeys(false))
            {
                if (!TFM_Util.isUniqueId(uuidString))
                {
                    TFM_Log.warning("Invalid Unique ID: " + uuidString + " in superadmin.yml, ignoring");
                    continue;
                }

                final UUID uuid = UUID.fromString(uuidString);

                final TFM_Admin superadmin = new TFM_Admin(uuid, section.getConfigurationSection(uuidString));
                adminList.put(uuid, superadmin);
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
        final File oldYaml = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE);
        final File newYaml = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE + ".bak");
        FileUtil.copy(oldYaml, newYaml);
    }

    public static void updateIndexLists()
    {
        superadminUUIDs.clear();
        telnetadminUUIDs.clear();
        senioradminUUIDs.clear();
        senioradminAliases.clear();
        superadminIps.clear();

        final Iterator<Entry<UUID, TFM_Admin>> it = adminList.entrySet().iterator();
        while (it.hasNext())
        {
            final Entry<UUID, TFM_Admin> pair = it.next();

            final UUID uuid = pair.getKey();
            TFM_Admin superadmin = pair.getValue();

            if (superadmin.isActivated())
            {
                superadminUUIDs.add(uuid);

                for (String ip : superadmin.getIps())
                {
                    superadminIps.add(ip);
                }

                if (superadmin.isSeniorAdmin())
                {
                    senioradminUUIDs.add(uuid);

                    for (String alias : superadmin.getConsoleAliases())
                    {
                        senioradminAliases.add(alias.toLowerCase());
                    }
                }

                if (superadmin.isTelnetAdmin())
                {
                    telnetadminUUIDs.add(uuid);
                }
            }
        }

        TFM_AdminWorld.getInstance().wipeAccessCache();
    }

    public static void saveSuperadminList()
    {
        try
        {
            updateIndexLists();

            YamlConfiguration config = new YamlConfiguration();

            config.set("clean_threshold_hours", cleanThreshold);

            Iterator<Entry<UUID, TFM_Admin>> it = adminList.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<UUID, TFM_Admin> pair = it.next();

                UUID uuid = pair.getKey();
                TFM_Admin superadmin = pair.getValue();

                config.set("superadmins." + uuid + ".last_login_name", superadmin.getLastLoginName());
                config.set("superadmins." + uuid + ".is_activated", superadmin.isActivated());
                config.set("superadmins." + uuid + ".last_login", TFM_Util.dateToString(superadmin.getLastLogin()));
                config.set("superadmins." + uuid + ".custom_login_message", superadmin.getCustomLoginMessage());
                config.set("superadmins." + uuid + ".is_senior_admin", superadmin.isSeniorAdmin());
                config.set("superadmins." + uuid + ".is_telnet_admin", superadmin.isTelnetAdmin());
                config.set("superadmins." + uuid + ".console_aliases", TFM_Util.removeDuplicates(superadmin.getConsoleAliases()));
                config.set("superadmins." + uuid + ".ips", TFM_Util.removeDuplicates(superadmin.getIps()));
            }

            config.save(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static TFM_Admin getAdminEntry(Player player)
    {
        final UUID uuid = player.getUniqueId();

        if (Bukkit.getOnlineMode())
        {
            if (adminList.containsKey(uuid))
            {
                return adminList.get(uuid);
            }
        }

        return getAdminEntryByIP(TFM_Util.getIp(player));
    }

    public static TFM_Admin getAdminEntry(UUID uuid)
    {
        return adminList.get(uuid);
    }

    @Deprecated
    public static TFM_Admin getAdminEntry(String name)
    {
        for (UUID uuid : adminList.keySet())
        {
            if (adminList.get(uuid).getLastLoginName().equalsIgnoreCase(name))
            {
                return adminList.get(uuid);
            }
        }
        return null;
    }

    public static TFM_Admin getAdminEntryByIP(String ip)
    {
        return getAdminEntryByIP(ip, false);
    }

    public static TFM_Admin getAdminEntryByIP(String needleIp, boolean fuzzy)
    {
        Iterator<Entry<UUID, TFM_Admin>> it = adminList.entrySet().iterator();
        while (it.hasNext())
        {
            final Entry<UUID, TFM_Admin> pair = it.next();
            final TFM_Admin superadmin = pair.getValue();

            if (fuzzy)
            {
                for (String haystackIp : superadmin.getIps())
                {
                    if (TFM_Util.fuzzyIpMatch(needleIp, haystackIp, 3))
                    {
                        return superadmin;
                    }
                }
            }
            else
            {
                if (superadmin.getIps().contains(needleIp))
                {
                    return superadmin;
                }
            }
        }
        return null;
    }

    public static void updateLastLogin(Player player)
    {
        final TFM_Admin admin = getAdminEntry(player);
        if (admin != null)
        {
            admin.setLastLogin(new Date());
            admin.setLastLoginName(player.getName());
            saveSuperadminList();
        }
    }

    public static boolean isSeniorAdmin(CommandSender user)
    {
        return isSeniorAdmin(user, false);
    }

    public static boolean isSeniorAdmin(CommandSender sender, boolean verifySuperadmin)
    {
        if (verifySuperadmin)
        {
            if (!isSuperAdmin(sender))
            {
                return false;
            }
        }


        if (!(sender instanceof Player))
        {
            return senioradminUUIDs.contains(((Player) sender).getUniqueId());
        }

        final TFM_Admin entry = getAdminEntry((Player) sender);
        if (entry != null)
        {
            return entry.isSeniorAdmin();
        }

        return false;
    }

    public static boolean isSuperAdmin(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        if (Bukkit.getOnlineMode())
        {
            if (superadminUUIDs.contains(((Player) sender).getUniqueId()));
            {
                return true;
            }
        }

        if (superadminIps.contains(TFM_Util.getIp((Player) sender)))
        {
            return true;
        }

        return false;
    }

    @Deprecated
    public static boolean checkPartialSuperadminIP(String ip, String name)
    {
        ip = ip.trim();

        if (superadminIps.contains(ip))
        {
            return true;
        }

        try
        {
            String matchIp = null;
            for (String testIp : superadminIps)
            {
                if (TFM_Util.fuzzyIpMatch(ip, testIp, 3))
                {
                    matchIp = testIp;
                    break;
                }
            }

            if (matchIp != null)
            {
                final TFM_Admin entry = getAdminEntryByIP(matchIp);

                if (entry == null)
                {
                    return true;
                }

                if (entry.getLastLoginName().equalsIgnoreCase(name))
                {
                    if (!entry.getIps().contains(ip))
                    {
                        entry.addIp(ip);
                    }
                    saveSuperadminList();
                }
                return true;

            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }

        return false;
    }

    public static boolean isAdminImpostor(Player player)
    {
        if (superadminUUIDs.contains(player.getUniqueId()))
        {
            return !isSuperAdmin(player);
        }

        return false;
    }

    /*public static void addSuperadmin(String username, List<String> ips)
     {
     try
     {
     username = username.toLowerCase();

     if (superadminList.containsKey(username))
     {
     TFM_Admin superadmin = superadminList.get(username);
     superadmin.setActivated(true);
     superadmin.getIps().addAll(ips);
     superadmin.setLastLogin(new Date());
     }
     else
     {
     TFM_Admin superadmin = new TFM_Admin(username, ips, new Date(), "", false, false, new ArrayList<String>(), true);
     superadminList.put(username.toLowerCase(), superadmin);
     }

     saveSuperadminList();
     }
     catch (Exception ex)
     {
     TFM_Log.severe(ex);
     }
     }*/
    public static void addSuperadmin(Player player)
    {
        try
        {
            final UUID uuid = player.getUniqueId();
            final String ip = TFM_Util.getIp(player);

            if (adminList.containsKey(uuid))
            {
                TFM_Admin superadmin = adminList.get(uuid);
                superadmin.setActivated(true);
                superadmin.addIp(TFM_Util.getIp(player));
                superadmin.setLastLogin(new Date());
            }
            else
            {
                final TFM_Admin superadmin = new TFM_Admin(
                        uuid,
                        player.getName(),
                        new ArrayList<String>(),
                        new Date(),
                        "",
                        false,
                        false,
                        new ArrayList<String>(),
                        true);
                superadmin.addIp(ip);
                adminList.put(uuid, superadmin);
            }

            saveSuperadminList();
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Cannot add superadmin: " + TFM_Util.formatPlayer(player));
            TFM_Log.severe(ex);
        }
    }

    public static void removeSuperadmin(OfflinePlayer player)
    {
        final UUID uuid = player.getUniqueId();

        if (adminList.containsKey(uuid))
        {
            TFM_Admin superadmin = adminList.get(uuid);
            superadmin.setActivated(false);
            Command_logs.deactivateSuperadmin(superadmin);
            saveSuperadminList();
        }
    }

    public static void cleanSuperadminList(boolean verbose)
    {
        Iterator<Entry<UUID, TFM_Admin>> it = adminList.entrySet().iterator();
        while (it.hasNext())
        {
            final Entry<UUID, TFM_Admin> pair = it.next();
            final TFM_Admin superadmin = pair.getValue();

            if (!superadmin.isActivated() || superadmin.isSeniorAdmin())
            {
                continue;
            }

            final Date lastLogin = superadmin.getLastLogin();
            final long lastLoginHours = TimeUnit.HOURS.convert(new Date().getTime() - lastLogin.getTime(), TimeUnit.MILLISECONDS);

            if (lastLoginHours > cleanThreshold)
            {
                if (verbose)
                {
                    TFM_Util.adminAction("TotalFreedomMod", "Deactivating superadmin " + superadmin.getLastLoginName() + ", inactive for " + lastLoginHours + " hours.", true);
                }

                superadmin.setActivated(false);
                Command_logs.deactivateSuperadmin(superadmin);
                TFM_TwitterHandler.getInstance().delTwitter(superadmin.getLastLoginName());
            }
        }
        saveSuperadminList();
    }

    @Deprecated
    public static boolean verifyIdentity(String username, String ip)
    {
        if (Bukkit.getOnlineMode())
        {
            return true;
        }

        TFM_Admin entry = getAdminEntry(username);
        if (entry != null)
        {
            return entry.getIps().contains(ip);
        }

        return false;
    }
}
