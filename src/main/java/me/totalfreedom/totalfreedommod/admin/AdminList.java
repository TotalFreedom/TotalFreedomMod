package me.totalfreedom.totalfreedommod.admin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.myzelyam.api.vanish.VanishAPI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class AdminList extends FreedomService
{
    @Getter
    private final Set<Admin> allAdmins = Sets.newHashSet(); // Includes disabled admins
    // Only active admins below
    @Getter
    private final Set<Admin> activeAdmins = Sets.newHashSet();
    private final Map<String, Admin> nameTable = Maps.newHashMap();
    private final Map<String, Admin> ipTable = Maps.newHashMap();
    public final List<String> verifiedNoAdmins = new ArrayList<>();
    public final Map<String, List<String>> verifiedNoAdminIps = Maps.newHashMap();

    @Override
    public void onStart()
    {
        load();
        deactivateOldEntries(false);
    }

    @Override
    public void onStop()
    {
    }

    public void load()
    {
        allAdmins.clear();
        try
        {
            ResultSet adminSet = plugin.sql.getAdminList();
            {
                while (adminSet.next())
                {
                    Admin admin = new Admin(adminSet);
                    allAdmins.add(admin);
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to load adminlist: " + e.getMessage());
        }

        updateTables();
        FLog.info("Loaded " + allAdmins.size() + " admins (" + nameTable.size() + " active,  " + ipTable.size() + " IPs)");
    }

    public void messageAllAdmins(String message)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (isAdmin(player))
            {
                player.sendMessage(message);
            }
        }
    }

    public synchronized boolean isAdminSync(CommandSender sender)
    {
        return isAdmin(sender);
    }

    public List<String> getActiveAdminNames()
    {
        List<String> names = new ArrayList();
        for (Admin admin : activeAdmins)
        {
            names.add(admin.getName());
        }
        return names;
    }

    public boolean isAdmin(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        Admin admin = getAdmin((Player)sender);

        return admin != null && admin.isActive();
    }

    public boolean isTelnetAdmin(CommandSender sender)
    {
        Admin admin = getAdmin(sender);
        if (admin == null)
        {
            return false;
        }

        return admin.getRank().ordinal() >= Rank.TELNET_ADMIN.ordinal();
    }

    public boolean isSeniorAdmin(CommandSender sender)
    {
        Admin admin = getAdmin(sender);
        if (admin == null)
        {
            return false;
        }

        return admin.getRank().ordinal() >= Rank.SENIOR_ADMIN.ordinal();
    }

    public Admin getAdmin(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getAdmin((Player)sender);
        }

        return getEntryByName(sender.getName());
    }

    public Admin getAdmin(Player player)
    {
        // Find admin
        String ip = FUtil.getIp(player);
        Admin admin = getEntryByName(player.getName());

        // Admin by name
        if (admin != null)
        {
            // Check if we're in online mode,
            // Or the players IP is in the admin entry
            if (Bukkit.getOnlineMode() || admin.getIps().contains(ip))
            {
                if (!admin.getIps().contains(ip))
                {
                    // Add the new IP if we have to
                    admin.addIp(ip);
                    save(admin);
                    updateTables();
                }
                return admin;
            }
        }

        // Admin by ip
        admin = getEntryByIp(ip);
        if (admin != null)
        {
            // Set the new username
            String oldName = admin.getName();
            admin.setName(player.getName());
            plugin.sql.updateAdminName(oldName, admin.getName());
            updateTables();
        }

        return null;
    }

    public Admin getEntryByName(String name)
    {
        return nameTable.get(name.toLowerCase());
    }

    public Admin getEntryByIp(String ip)
    {
        return ipTable.get(ip);
    }

    public Admin getEntryByIpFuzzy(String needleIp)
    {
        final Admin directAdmin = getEntryByIp(needleIp);
        if (directAdmin != null)
        {
            return directAdmin;
        }

        for (String ip : ipTable.keySet())
        {
            if (FUtil.fuzzyIpMatch(needleIp, ip, 3))
            {
                return ipTable.get(ip);
            }
        }

        return null;
    }

    public void updateLastLogin(Player player)
    {
        final Admin admin = getAdmin(player);
        if (admin == null)
        {
            return;
        }

        admin.setLastLogin(new Date());
        admin.setName(player.getName());
        save(admin);
    }

    public boolean isAdminImpostor(Player player)
    {
        return getEntryByName(player.getName()) != null && !isAdmin(player) && !isVerifiedAdmin(player);
    }

    public boolean isVerifiedAdmin(Player player)
    {
        return verifiedNoAdmins.contains(player.getName()) && verifiedNoAdminIps.get(player.getName()).contains(FUtil.getIp(player));
    }

    public boolean isIdentityMatched(Player player)
    {
        if (Bukkit.getOnlineMode())
        {
            return true;
        }

        Admin admin = getAdmin(player);
        return admin == null ? false : admin.getName().equalsIgnoreCase(player.getName());
    }

    public boolean addAdmin(Admin admin)
    {
        if (!admin.isValid())
        {
            logger.warning("Could not add admin: " + admin.getName() + " Admin is missing details!");
            return false;
        }

        // Store admin, update views
        allAdmins.add(admin);
        updateTables();

        // Save admin
        plugin.sql.addAdmin(admin);

        return true;
    }

    public boolean removeAdmin(Admin admin)
    {
        if (admin.getRank().isAtLeast(Rank.TELNET_ADMIN))
        {
            if (plugin.btb != null)
            {
                plugin.btb.killTelnetSessions(admin.getName());
            }
        }

        // Remove admin, update views
        if (!allAdmins.remove(admin))
        {
            return false;
        }
        updateTables();

        // Unsave admin
        plugin.sql.removeAdmin(admin);

        return true;
    }

    public void updateTables()
    {
        activeAdmins.clear();
        nameTable.clear();
        ipTable.clear();

        for (Admin admin : allAdmins)
        {
            if (!admin.isActive())
            {
                continue;
            }

            activeAdmins.add(admin);
            nameTable.put(admin.getName().toLowerCase(), admin);

            for (String ip : admin.getIps())
            {
                ipTable.put(ip, admin);
            }

        }
    }

    public Set<String> getAdminNames()
    {
        return nameTable.keySet();
    }

    public Set<String> getAdminIps()
    {
        return ipTable.keySet();
    }

    public void save(Admin admin)
    {
        try
        {
            ResultSet currentSave = plugin.sql.getAdminByName(admin.getName());
            for (Map.Entry<String, Object> entry : admin.toSQLStorable().entrySet())
            {
                Object storedValue = plugin.sql.getValue(currentSave, entry.getKey(), entry.getValue());
                if (storedValue != null && !storedValue.equals(entry.getValue()) || storedValue == null && entry.getValue() != null || entry.getValue() == null)
                {
                    plugin.sql.setAdminValue(admin, entry.getKey(), entry.getValue());
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to save admin: " + e.getMessage());
        }
    }

    public void deactivateOldEntries(boolean verbose)
    {
        for (Admin admin : allAdmins)
        {
            if (!admin.isActive() || admin.getRank().isAtLeast(Rank.SENIOR_ADMIN))
            {
                continue;
            }

            final Date lastLogin = admin.getLastLogin();
            final long lastLoginHours = TimeUnit.HOURS.convert(new Date().getTime() - lastLogin.getTime(), TimeUnit.MILLISECONDS);

            if (lastLoginHours < ConfigEntry.ADMINLIST_CLEAN_THESHOLD_HOURS.getInteger())
            {
                continue;
            }

            if (verbose)
            {
                FUtil.adminAction("TotalFreedomMod", "Deactivating admin " + admin.getName() + ", inactive for " + lastLoginHours + " hours", true);
            }

            admin.setActive(false);
            save(admin);
        }

        updateTables();
    }

    public boolean isVanished(Player player)
    {
        return VanishAPI.isInvisible(player);
    }
}