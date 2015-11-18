package me.totalfreedom.totalfreedommod.admin;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.commands.Command_logs;
import me.totalfreedom.totalfreedommod.config.FConfig;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.component.service.AbstractService;
import net.pravian.aero.util.Ips;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.ServicePriority;

public class AdminList extends AbstractService<TotalFreedomMod>
{
    @Getter
    private final Map<String, Admin> allAdmins = Maps.newHashMap(); // Includes disabled admins
    // Only active admins below
    @Getter
    private final Set<Admin> activeAdmins = Sets.newHashSet();
    private final Map<String, Admin> nameTable = Maps.newHashMap();
    private final Map<String, Admin> ipTable = Maps.newHashMap();
    //
    private int cleanThreshold = 24 * 7; // 1 Week in hours
    private final FConfig config;

    public AdminList(TotalFreedomMod plugin)
    {
        super(plugin);

        this.config = new FConfig(TotalFreedomMod.plugin, TotalFreedomMod.SUPERADMIN_FILENAME, true);
    }

    @Override
    protected void onStart()
    {
        load();

        server.getServicesManager().register(Function.class, new Function<Player, Boolean>()
        {
            @Override
            public Boolean apply(Player player)
            {
                return isAdmin(player);
            }
        }, plugin, ServicePriority.Normal);
    }

    @Override
    protected void onStop()
    {
        save();
    }

    public void load()
    {
        config.load();

        allAdmins.clear();
        for (String key : config.getKeys(false))
        {
            Admin admin = new Admin(key);
            admin.loadFrom(config.getConfigurationSection(key));

            if (!admin.isValid())
            {
                FLog.warning("Could not load admin: " + key + ". Missing details!");
                continue;
            }

            allAdmins.put(key, admin);
        }

        updateTables();
        FLog.info("Loaded " + nameTable.size() + " admins (" + nameTable.size() + " active) and " + ipTable.size() + " IPs.");
    }

    public void save()
    {
        // Clear the config
        for (String key : config.getKeys(false))
        {
            config.set(key, null);
        }

        for (Admin admin : allAdmins.values())
        {
            admin.saveTo(config.createSection(admin.getConfigKey()));
        }

        config.save();
    }

    public void save(Admin admin)
    {
        if (!allAdmins.values().contains(admin))
        { // Ensure admin is present
            addAdmin(admin, false);
        }

        admin.saveTo(config.createSection(admin.getConfigKey()));
        config.save();
    }

    public synchronized boolean isAdminSync(CommandSender sender)
    {
        return isAdmin(sender);
    }

    public boolean isAdmin(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        return getAdmin((Player) sender) != null;
    }

    public boolean isSeniorAdmin(CommandSender sender)
    {
        Admin admin = getAdmin(sender);
        if (admin == null)
        {
            return false;
        }

        return admin.getRank().ordinal() >= PlayerRank.SENIOR_ADMIN.ordinal();
    }

    public Admin getAdmin(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getAdmin((Player) sender);
        }

        return getEntryByName(sender.getName());
    }

    public Admin getAdmin(Player player)
    {
        String ip = Ips.getIp(player);
        Admin admin = getEntryByIp(ip, true);

        if (admin == null && Bukkit.getOnlineMode())
        {
            admin = getEntryByName(player.getName());

            // Add new IP
            if (admin != null)
            {
                admin.addIp(ip);
                save(admin);
            }
        }

        return admin;
    }

    public Admin getEntryByName(String name)
    {
        return nameTable.get(name.toLowerCase());
    }

    public Admin getEntryByIp(String ip)
    {
        return getEntryByIp(ip, false);
    }

    public Admin getEntryByIp(String needleIp, boolean fuzzy)
    {
        Admin admin = ipTable.get(needleIp);

        if (admin != null || !fuzzy)
        {
            return admin;
        }

        for (String ip : ipTable.keySet())
        {
            if (FUtil.fuzzyIpMatch(needleIp, ip, 3))
            {
                return ipTable.get(ip);
            }
        }
        return admin;
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
        return getEntryByName(player.getName()) != null && !isAdmin(player);
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
        return addAdmin(admin, false);
    }

    public boolean addAdmin(Admin admin, boolean overwrite)
    {
        final String key = admin.getConfigKey();

        if (!overwrite && allAdmins.containsKey(key))
        {
            return false;
        }

        // Store admin, update views
        allAdmins.put(key, admin);
        updateTables();

        // Save admin
        admin.saveTo(config.createSection(key));
        config.save();

        return true;
    }

    public boolean removeAdmin(Admin admin)
    {

        // Remove admin, update views
        if (allAdmins.remove(admin.getName().toLowerCase()) == null)
        {
            return false;
        }
        updateTables();

        // 'Unsave' admin
        config.set(admin.getConfigKey(), null);
        config.save();

        return true;
    }

    public void updateTables()
    {
        activeAdmins.clear();
        nameTable.clear();
        ipTable.clear();

        for (Admin admin : allAdmins.values())
        {
            if (!admin.isActivated())
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

        plugin.wm.adminworld.wipeAccessCache();
    }

    public Set<String> getAdminNames()
    {
        return nameTable.keySet();
    }

    public Set<String> getAdminIps()
    {
        return ipTable.keySet();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);

        boolean isAdmin = plugin.al.isAdmin(player);
        if (isAdmin)
        {
            // Verify strict IP match
            if (!plugin.al.isIdentityMatched(player))
            {
                fPlayer.setSuperadminIdVerified(false);
                FUtil.bcastMsg("Warning: " + player.getName() + " is an admin, but is using an account not registered to one of their ip-list.", ChatColor.RED);
            }
            else
            {
                fPlayer.setSuperadminIdVerified(true);
                plugin.al.updateLastLogin(player);
            }
        }
    }

    /*
     public void addAdmin(OfflinePlayer player)
     {
     final String name = player.getName().toLowerCase();
     final String ip = TFM_Util.getIp(player);
     final boolean canSuperIp = !TFM_MainConfig.getList(TFM_ConfigEntry.NOADMIN_IPS).contains(ip);

     if (nameTable.containsKey(name))
     {
     final Admin superadmin = nameTable.;
     superadmin.setActivated(true);

     if (player.isOnline())
     {
     superadmin.setLastLogin(new Date());

     if (ip != null && canSuperIp)
     {
     superadmin.addIp(ip);
     }
     }

     save();
     updateTables();
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

     final Admin superadmin = new Admin(
     uuid,
     player.getName(),
     new Date(),
     "",
     false,
     false,
     true);
     superadmin.addIp(ip);

     nameTable.put(uuid, superadmin);

     updateTables();
     }

     public void removeAdmin(OfflinePlayer player)
     {
     final UUID uuid = TFM_UuidManager.getUniqueId(player);

     if (!nameTable.containsKey(uuid))
     {
     TFM_Log.warning("Could not remove admin: " + TFM_Util.formatPlayer(player));
     TFM_Log.warning("Player is not an admin!");
     return;
     }

     final Admin superadmin = nameTable.get(uuid);
     superadmin.setActivated(false);
     Command_logs.deactivateSuperadmin(superadmin);

     updateTables();
     }*/
    public void deactivateOldEntries(boolean verbose)
    {
        for (Admin admin : allAdmins.values())
        {
            if (!admin.isActivated() || admin.getRank() == PlayerRank.SENIOR_ADMIN)
            {
                continue;
            }

            final Date lastLogin = admin.getLastLogin();
            final long lastLoginHours = TimeUnit.HOURS.convert(new Date().getTime() - lastLogin.getTime(), TimeUnit.MILLISECONDS);

            if (lastLoginHours < cleanThreshold)
            {
                continue;
            }

            if (verbose)
            {
                FUtil.adminAction("TotalFreedomMod", "Deactivating superadmin " + admin.getName() + ", inactive for " + lastLoginHours + " hours.", true);
            }

            admin.setActivated(false);
            Command_logs.deactivateSuperadmin(admin);
        }

        save();
        updateTables();
    }
}
