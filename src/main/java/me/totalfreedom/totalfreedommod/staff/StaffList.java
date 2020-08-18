package me.totalfreedom.totalfreedommod.staff;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

public class StaffList extends FreedomService
{
    @Getter
    private final Set<StaffMember> allStaffMembers = Sets.newHashSet(); // Includes disabled staff
    // Only active staff below
    @Getter
    private final Set<StaffMember> activeStaffMembers = Sets.newHashSet();
    private final Map<String, StaffMember> nameTable = Maps.newHashMap();
    private final Map<String, StaffMember> ipTable = Maps.newHashMap();
    public final List<String> verifiedNoStaff = new ArrayList<>();
    public final Map<String, List<String>> verifiedNoStaffIps = Maps.newHashMap();
    public static final List<String> vanished = new ArrayList<>();

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
        allStaffMembers.clear();
        try
        {
            ResultSet adminSet = plugin.sql.getStaffList();
            {
                while (adminSet.next())
                {
                    StaffMember staffMember = new StaffMember(adminSet);
                    allStaffMembers.add(staffMember);
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to load staff list: " + e.getMessage());
        }

        updateTables();
        FLog.info("Loaded " + allStaffMembers.size() + " staff members (" + nameTable.size() + " active,  " + ipTable.size() + " IPs)");
    }

    public void messageAllStaff(String message)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (isStaff(player))
            {
                player.sendMessage(message);
            }
        }
    }

    public synchronized boolean isStaffSync(CommandSender sender)
    {
        return isStaff(sender);
    }

    public List<String> getActiveStaffNames()
    {
        List<String> names = new ArrayList();
        for (StaffMember staffMember : activeStaffMembers)
        {
            names.add(staffMember.getName());
        }
        return names;
    }

    public boolean isStaff(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        StaffMember staffMember = getAdmin((Player)sender);

        return staffMember != null && staffMember.isActive();
    }

    public boolean isStaff(Player player)
    {
        if (player == null)
        {
            return true;
        }

        StaffMember staffMember = getAdmin(player);

        return staffMember != null && staffMember.isActive();
    }

    public boolean isMod(CommandSender sender)
    {
        StaffMember staffMember = getAdmin(sender);
        if (staffMember == null)
        {
            return false;
        }

        return staffMember.getRank().ordinal() >= Rank.MOD.ordinal();
    }

    public boolean isAdmin(CommandSender sender)
    {
        StaffMember staffMember = getAdmin(sender);
        if (staffMember == null)
        {
            return false;
        }

        return staffMember.getRank().ordinal() >= Rank.ADMIN.ordinal();
    }

    public StaffMember getAdmin(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getAdmin((Player)sender);
        }

        return getEntryByName(sender.getName());
    }

    public StaffMember getAdmin(Player player)
    {
        // Find admin
        String ip = FUtil.getIp(player);
        StaffMember staffMember = getEntryByName(player.getName());

        // Admin by name
        if (staffMember != null)
        {
            // Check if we're in online mode,
            // Or the players IP is in the admin entry
            if (Bukkit.getOnlineMode() || staffMember.getIps().contains(ip))
            {
                if (!staffMember.getIps().contains(ip))
                {
                    // Add the new IP if we have to
                    staffMember.addIp(ip);
                    save(staffMember);
                    updateTables();
                }
                return staffMember;
            }
        }

        // Admin by ip
        staffMember = getEntryByIp(ip);
        if (staffMember != null)
        {
            // Set the new username
            String oldName = staffMember.getName();
            staffMember.setName(player.getName());
            plugin.sql.updateStaffMemberName(oldName, staffMember.getName());
            updateTables();
        }

        return null;
    }

    public StaffMember getEntryByName(String name)
    {
        return nameTable.get(name.toLowerCase());
    }

    public StaffMember getEntryByIp(String ip)
    {
        return ipTable.get(ip);
    }

    public StaffMember getEntryByIpFuzzy(String needleIp)
    {
        final StaffMember directStaffMember = getEntryByIp(needleIp);
        if (directStaffMember != null)
        {
            return directStaffMember;
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
        final StaffMember staffMember = getAdmin(player);
        if (staffMember == null)
        {
            return;
        }

        staffMember.setLastLogin(new Date());
        staffMember.setName(player.getName());
        save(staffMember);
    }

    public boolean isStaffImpostor(Player player)
    {
        return getEntryByName(player.getName()) != null && !isStaff(player) && !isVerifiedStaff(player);
    }

    public boolean isVerifiedStaff(Player player)
    {
        return verifiedNoStaff.contains(player.getName()) && verifiedNoStaffIps.get(player.getName()).contains(FUtil.getIp(player));
    }

    public boolean isIdentityMatched(Player player)
    {
        if (Bukkit.getOnlineMode())
        {
            return true;
        }

        StaffMember staffMember = getAdmin(player);
        return staffMember == null ? false : staffMember.getName().equalsIgnoreCase(player.getName());
    }

    public boolean addAdmin(StaffMember staffMember)
    {
        if (!staffMember.isValid())
        {
            logger.warning("Could not add staff: " + staffMember.getName() + " Staff is missing details!");
            return false;
        }

        // Store admin, update views
        allStaffMembers.add(staffMember);
        updateTables();

        // Save admin
        plugin.sql.addStaffMember(staffMember);

        return true;
    }

    public boolean removeAdmin(StaffMember staffMember)
    {
        if (staffMember.getRank().isAtLeast(Rank.MOD))
        {
            if (plugin.btb != null)
            {
                plugin.btb.killTelnetSessions(staffMember.getName());
            }
        }

        // Remove staff, update views
        if (!allStaffMembers.remove(staffMember))
        {
            return false;
        }
        updateTables();

        // Unsave staff
        plugin.sql.removeStaffMember(staffMember);

        return true;
    }

    public void updateTables()
    {
        activeStaffMembers.clear();
        nameTable.clear();
        ipTable.clear();

        for (StaffMember staffMember : allStaffMembers)
        {
            if (!staffMember.isActive())
            {
                continue;
            }

            activeStaffMembers.add(staffMember);
            nameTable.put(staffMember.getName().toLowerCase(), staffMember);

            for (String ip : staffMember.getIps())
            {
                ipTable.put(ip, staffMember);
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

    public void save(StaffMember staffMember)
    {
        try
        {
            ResultSet currentSave = plugin.sql.getStaffMemberByName(staffMember.getName());
            for (Map.Entry<String, Object> entry : staffMember.toSQLStorable().entrySet())
            {
                Object storedValue = plugin.sql.getValue(currentSave, entry.getKey(), entry.getValue());
                if (storedValue != null && !storedValue.equals(entry.getValue()) || storedValue == null && entry.getValue() != null || entry.getValue() == null)
                {
                    plugin.sql.setStaffMemberValue(staffMember, entry.getKey(), entry.getValue());
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to save staff: " + e.getMessage());
        }
    }

    public void deactivateOldEntries(boolean verbose)
    {
        for (StaffMember staffMember : allStaffMembers)
        {
            if (!staffMember.isActive() || staffMember.getRank().isAtLeast(Rank.ADMIN))
            {
                continue;
            }

            final Date lastLogin = staffMember.getLastLogin();
            final long lastLoginHours = TimeUnit.HOURS.convert(new Date().getTime() - lastLogin.getTime(), TimeUnit.MILLISECONDS);

            if (lastLoginHours < ConfigEntry.STAFFLIST_CLEAN_THESHOLD_HOURS.getInteger())
            {
                continue;
            }

            if (verbose)
            {
                FUtil.staffAction("TotalFreedomMod", "Deactivating staff member " + staffMember.getName() + ", inactive for " + lastLoginHours + " hours", true);
            }

            staffMember.setActive(false);
            save(staffMember);
        }

        updateTables();
    }

    public boolean isVanished(String player)
    {
        return vanished.contains(player);
    }
}