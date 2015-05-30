package me.StevenLawson.TotalFreedomMod;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.StevenLawson.TotalFreedomMod.Commands.Command_logs;
import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.Config.TFM_MainConfig;
import me.StevenLawson.TotalFreedomMod.World.TFM_AdminWorld;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class TFM_AdminList
{
    public static final Function<Player, Boolean> SUPERADMIN_SERVICE;
    private static final Map<UUID, TFM_Admin> adminList;
    private static final Set<UUID> superUUIDs;
    private static final Set<UUID> telnetUUIDs;
    private static final Set<UUID> seniorUUIDs;
    private static final Set<String> seniorConsoleNames;
    private static final Set<String> superIps;
    private static int cleanThreshold = 24 * 7; // 1 Week in hours

    static
    {
        adminList = new HashMap<UUID, TFM_Admin>();
        superUUIDs = new HashSet<UUID>();
        telnetUUIDs = new HashSet<UUID>();
        seniorUUIDs = new HashSet<UUID>();
        seniorConsoleNames = new HashSet<String>();
        superIps = new HashSet<String>();

        SUPERADMIN_SERVICE = new Function<Player, Boolean>()
        {

            @Override
            public Boolean apply(Player f)
            {
                return isSuperAdmin(f);
            }
        };
    }

    private TFM_AdminList()
    {
        throw new AssertionError();
    }

    public static Set<UUID> getSuperUUIDs()
    {
        return Collections.unmodifiableSet(superUUIDs);
    }

    public static Set<UUID> getTelnetUUIDs()
    {
        return Collections.unmodifiableSet(telnetUUIDs);
    }

    public static Set<UUID> getSeniorUUIDs()
    {
        return Collections.unmodifiableSet(seniorUUIDs);
    }

    public static Set<String> getSeniorConsoleNames()
    {
        return Collections.unmodifiableSet(seniorConsoleNames);
    }

    public static Set<String> getSuperadminIps()
    {
        return Collections.unmodifiableSet(superIps);
    }

    public static Set<TFM_Admin> getAllAdmins()
    {
        return Sets.newHashSet(adminList.values());
    }

    public static Set<String> getSuperNames()
    {
        final Set<String> names = new HashSet<String>();

        for (TFM_Admin admin : adminList.values())
        {
            if (!admin.isActivated())
            {
                continue;
            }

            names.add(admin.getLastLoginName());
        }

        return Collections.unmodifiableSet(names);
    }

    public static Set<String> getLowercaseSuperNames()
    {
        final Set<String> names = new HashSet<String>();

        for (TFM_Admin admin : adminList.values())
        {
            if (!admin.isActivated())
            {
                continue;
            }

            names.add(admin.getLastLoginName().toLowerCase());
        }

        return Collections.unmodifiableSet(names);
    }

    public static void setUuid(TFM_Admin admin, UUID oldUuid, UUID newUuid)
    {
        if (!adminList.containsKey(oldUuid))
        {
            TFM_Log.warning("Could not set new UUID for admin " + admin.getLastLoginName() + ", admin is not loaded!");
            return;
        }

        if (oldUuid.equals(newUuid))
        {
            TFM_Log.warning("could not set new UUID for admin " + admin.getLastLoginName() + ", UUIDs match.");
            return;
        }

        // Add new entry
        final TFM_Admin newAdmin = new TFM_Admin(
                newUuid,
                admin.getLastLoginName(),
                admin.getLastLogin(),
                admin.getCustomLoginMessage(),
                admin.isTelnetAdmin(),
                admin.isSeniorAdmin(),
                admin.isActivated());
        newAdmin.addIps(admin.getIps());
        adminList.put(newUuid, newAdmin);
        save(newAdmin);

        // Remove old entry
        adminList.remove(oldUuid);
        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, TotalFreedomMod.SUPERADMIN_FILENAME, true);
        config.load();
        config.set("admins." + oldUuid.toString(), null);
        config.save();
    }

    public static void load()
    {
        adminList.clear();

        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, TotalFreedomMod.SUPERADMIN_FILENAME, true);
        config.load();

        cleanThreshold = config.getInt("clean_threshold_hours", cleanThreshold);

        // Parse old superadmins
        if (config.isConfigurationSection("superadmins"))
        {
            parseOldConfig(config);
        }

        if (!config.isConfigurationSection("admins"))
        {
            TFM_Log.warning("Missing admins section in superadmin.yml.");
            return;
        }

        final ConfigurationSection section = config.getConfigurationSection("admins");

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

        TFM_Log.info("Loaded " + adminList.size() + " admins (" + superUUIDs.size() + " active) and " + superIps.size() + " IPs.");
    }

    public static void updateIndexLists()
    {
        superUUIDs.clear();
        telnetUUIDs.clear();
        seniorUUIDs.clear();
        seniorConsoleNames.clear();
        superIps.clear();

        for (TFM_Admin admin : adminList.values())
        {
            if (!admin.isActivated())
            {
                continue;
            }

            final UUID uuid = admin.getUniqueId();

            superUUIDs.add(uuid);

            for (String ip : admin.getIps())
            {
                superIps.add(ip);
            }

            if (admin.isTelnetAdmin())
            {
                telnetUUIDs.add(uuid);
            }

            if (admin.isSeniorAdmin())
            {
                seniorUUIDs.add(uuid);

                seniorConsoleNames.add(admin.getLastLoginName());
                for (String alias : admin.getConsoleAliases())
                {
                    seniorConsoleNames.add(alias.toLowerCase());
                }
            }
        }

        TFM_AdminWorld.getInstance().wipeAccessCache();
    }

    private static void parseOldConfig(TFM_Config config)
    {
        TFM_Log.info("Old superadmin configuration found, parsing...");

        final ConfigurationSection section = config.getConfigurationSection("superadmins");

        int counter = 0;
        int errors = 0;

        for (String admin : config.getConfigurationSection("superadmins").getKeys(false))
        {
            final UUID uuid = TFM_UuidManager.getUniqueId(admin);

            if (uuid == null)
            {
                errors++;
                TFM_Log.warning("Could not convert admin " + admin + ", UUID could not be found!");
                continue;
            }

            config.set("admins." + uuid + ".last_login_name", uuid);
            config.set("admins." + uuid + ".is_activated", section.getBoolean(admin + ".is_activated"));
            config.set("admins." + uuid + ".is_telnet_admin", section.getBoolean(admin + ".is_telnet_admin"));
            config.set("admins." + uuid + ".is_senior_admin", section.getBoolean(admin + ".is_senior_admin"));
            config.set("admins." + uuid + ".last_login", section.getString(admin + ".last_login"));
            config.set("admins." + uuid + ".custom_login_message", section.getString(admin + ".custom_login_message"));
            config.set("admins." + uuid + ".console_aliases", section.getStringList(admin + ".console_aliases"));
            config.set("admins." + uuid + ".ips", section.getStringList(admin + ".ips"));

            counter++;
        }

        config.set("superadmins", null);
        config.save();

        TFM_Log.info("Done! " + counter + " admins parsed, " + errors + " errors");
    }

    public static void saveAll()
    {
        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, TotalFreedomMod.SUPERADMIN_FILENAME, true);
        config.load();

        config.set("clean_threshold_hours", cleanThreshold);

        final Iterator<Entry<UUID, TFM_Admin>> it = adminList.entrySet().iterator();
        while (it.hasNext())
        {
            final Entry<UUID, TFM_Admin> pair = it.next();

            final UUID uuid = pair.getKey();
            final TFM_Admin superadmin = pair.getValue();

            config.set("admins." + uuid + ".last_login_name", superadmin.getLastLoginName());
            config.set("admins." + uuid + ".is_activated", superadmin.isActivated());
            config.set("admins." + uuid + ".is_telnet_admin", superadmin.isTelnetAdmin());
            config.set("admins." + uuid + ".is_senior_admin", superadmin.isSeniorAdmin());
            config.set("admins." + uuid + ".last_login", TFM_Util.dateToString(superadmin.getLastLogin()));
            config.set("admins." + uuid + ".custom_login_message", superadmin.getCustomLoginMessage());
            config.set("admins." + uuid + ".console_aliases", TFM_Util.removeDuplicates(superadmin.getConsoleAliases()));
            config.set("admins." + uuid + ".ips", TFM_Util.removeDuplicates(superadmin.getIps()));
        }

        config.save();
    }

    public static void save(TFM_Admin admin)
    {
        if (!adminList.containsValue(admin))
        {
            TFM_Log.warning("Could not save admin " + admin.getLastLoginName() + ", admin is not loaded!");
            return;
        }

        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, TotalFreedomMod.SUPERADMIN_FILENAME, true);
        config.load();

        final UUID uuid = admin.getUniqueId();

        config.set("admins." + uuid + ".last_login_name", admin.getLastLoginName());
        config.set("admins." + uuid + ".is_activated", admin.isActivated());
        config.set("admins." + uuid + ".is_telnet_admin", admin.isTelnetAdmin());
        config.set("admins." + uuid + ".is_senior_admin", admin.isSeniorAdmin());
        config.set("admins." + uuid + ".last_login", TFM_Util.dateToString(admin.getLastLogin()));
        config.set("admins." + uuid + ".custom_login_message", admin.getCustomLoginMessage());
        config.set("admins." + uuid + ".console_aliases", TFM_Util.removeDuplicates(admin.getConsoleAliases()));
        config.set("admins." + uuid + ".ips", TFM_Util.removeDuplicates(admin.getIps()));

        config.save();
    }

    public static TFM_Admin getEntry(Player player)
    {
        return getEntry(TFM_UuidManager.getUniqueId(player));
    }

    public static TFM_Admin getEntry(UUID uuid)
    {
        return adminList.get(uuid);
    }

    @Deprecated
    public static TFM_Admin getEntry(String name)
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

    public static TFM_Admin getEntryByIp(String ip)
    {
        return getEntryByIp(ip, false);
    }

    public static TFM_Admin getEntryByIp(String needleIp, boolean fuzzy)
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
        final TFM_Admin admin = getEntry(player);
        if (admin == null)
        {
            return;
        }
        admin.setLastLogin(new Date());
        admin.setLastLoginName(player.getName());
        saveAll();
    }

    public static boolean isSuperAdminSafe(UUID uuid, String ip)
    {
        if (TotalFreedomMod.server.getOnlineMode() && uuid != null)
        {
            return TFM_AdminList.getSuperUUIDs().contains(uuid);
        }

        final TFM_Admin admin = TFM_AdminList.getEntryByIp(ip);
        return admin != null && admin.isActivated();
    }

    public static synchronized boolean isSuperAdminSync(CommandSender sender)
    {
        return isSuperAdmin(sender);
    }

    public static boolean isSuperAdmin(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        final Player player = (Player) sender;

        if (superIps.contains(TFM_Util.getIp(player)))
        {
            return true;
        }

        if (Bukkit.getOnlineMode() && superUUIDs.contains(TFM_UuidManager.getUniqueId(player)))
        {
            return true;
        }

        return false;
    }

    public static boolean isTelnetAdmin(CommandSender sender, boolean verifySuperadmin)
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
            return true;
        }

        final TFM_Admin entry = getEntry((Player) sender);
        if (entry != null)
        {
            return entry.isTelnetAdmin();
        }

        return false;
    }

    public static boolean isSeniorAdmin(CommandSender sender)
    {
        return isSeniorAdmin(sender, false);
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
            return seniorConsoleNames.contains(sender.getName())
                    || (TFM_MainConfig.getBoolean(TFM_ConfigEntry.CONSOLE_IS_SENIOR) && sender.getName().equals("CONSOLE"));
        }

        final TFM_Admin entry = getEntry((Player) sender);
        if (entry != null)
        {
            return entry.isSeniorAdmin();
        }

        return false;
    }

    public static boolean isIdentityMatched(Player player)
    {
        if (!isSuperAdmin(player))
        {
            return false;
        }

        if (Bukkit.getOnlineMode())
        {
            return true;
        }

        final TFM_Admin entry = getEntry(player);
        if (entry == null)
        {
            return false;
        }

        return entry.getUniqueId().equals(TFM_UuidManager.getUniqueId(player));
    }

    @Deprecated
    public static boolean checkPartialSuperadminIp(String ip, String name)
    {
        ip = ip.trim();

        if (superIps.contains(ip))
        {
            return true;
        }

        try
        {
            String matchIp = null;
            for (String testIp : superIps)
            {
                if (TFM_Util.fuzzyIpMatch(ip, testIp, 3))
                {
                    matchIp = testIp;
                    break;
                }
            }

            if (matchIp != null)
            {
                final TFM_Admin entry = getEntryByIp(matchIp);

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
                    saveAll();
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
        if (superUUIDs.contains(TFM_UuidManager.getUniqueId(player)))
        {
            return !isSuperAdmin(player);
        }

        return false;
    }

    public static void addSuperadmin(OfflinePlayer player)
    {
        final UUID uuid = TFM_UuidManager.getUniqueId(player);
        final String ip = TFM_Util.getIp(player);
        final boolean canSuperIp = !TFM_MainConfig.getList(TFM_ConfigEntry.NOADMIN_IPS).contains(ip);

        if (adminList.containsKey(uuid))
        {
            final TFM_Admin superadmin = adminList.get(uuid);
            superadmin.setActivated(true);

            if (player.isOnline())
            {
                superadmin.setLastLogin(new Date());

                if (ip != null && canSuperIp)
                {
                    superadmin.addIp(ip);
                }
            }

            saveAll();
            updateIndexLists();
            return;
        }

        if (ip == null)
        {
            TFM_Log.severe("Could not add superadmin: " + TFM_Util.formatPlayer(player));
            TFM_Log.severe("Could not retrieve IP!");
            return;
        }

        if (!canSuperIp)
        {
            TFM_Log.warning("Could not add superadmin: " + TFM_Util.formatPlayer(player));
            TFM_Log.warning("IP " + ip + " may not be supered.");
            return;
        }

        final TFM_Admin superadmin = new TFM_Admin(
                uuid,
                player.getName(),
                new Date(),
                "",
                false,
                false,
                true);
        superadmin.addIp(ip);

        adminList.put(uuid, superadmin);

        saveAll();
        updateIndexLists();
    }

    public static void removeSuperadmin(OfflinePlayer player)
    {
        final UUID uuid = TFM_UuidManager.getUniqueId(player);

        if (!adminList.containsKey(uuid))
        {
            TFM_Log.warning("Could not remove admin: " + TFM_Util.formatPlayer(player));
            TFM_Log.warning("Player is not an admin!");
            return;
        }

        final TFM_Admin superadmin = adminList.get(uuid);
        superadmin.setActivated(false);
        Command_logs.deactivateSuperadmin(superadmin);

        saveAll();
        updateIndexLists();
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
                TFM_TwitterHandler.delTwitter(superadmin.getLastLoginName());
            }
        }

        saveAll();
        updateIndexLists();
    }
}
