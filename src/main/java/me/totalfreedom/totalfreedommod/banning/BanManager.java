package me.totalfreedom.totalfreedommod.banning;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class BanManager extends FreedomService
{

    private final Set<Ban> bans = Sets.newHashSet();
    private final Map<String, Ban> nameBans = Maps.newHashMap();
    private final Map<UUID, Ban> uuidBans = Maps.newHashMap();
    private final Map<String, Ban> ipBans = Maps.newHashMap();
    private final List<String> unbannableUsernames = Lists.newArrayList();

    //

    @Override
    public void onStart()
    {
        bans.clear();
        try
        {
            ResultSet banSet = plugin.sql.getBanList();
            {
                while (banSet.next())
                {
                    String name = banSet.getString("name");
                    UUID uuid = null;
                    String strUUID = banSet.getString("uuid");
                    if (strUUID != null)
                    {
                        uuid = UUID.fromString(strUUID);
                    }
                    List<String> ips = FUtil.stringToList(banSet.getString("ips"));
                    String by = banSet.getString("by");
                    Date at = new Date(banSet.getLong("at"));
                    Date expires = new Date(banSet.getLong("expires"));
                    String reason = banSet.getString("reason");
                    Ban ban = new Ban(name, uuid, ips, by, at, expires, reason);
                    bans.add(ban);
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to load ban list: " + e.getMessage());
        }

        // Remove expired bans, repopulate ipBans and nameBans,
        updateViews();

        FLog.info("Loaded " + ipBans.size() + " IP bans and " + nameBans.size() + " username bans.");

        // Load unbannable usernames
        unbannableUsernames.clear();
        unbannableUsernames.addAll((Collection<? extends String>)ConfigEntry.FAMOUS_PLAYERS.getList());
        FLog.info("Loaded " + unbannableUsernames.size() + " unbannable usernames.");
    }

    @Override
    public void onStop()
    {
    }

    public Set<Ban> getAllBans()
    {
        return Collections.unmodifiableSet(bans);
    }

    public Collection<Ban> getIpBans()
    {
        return Collections.unmodifiableCollection(ipBans.values());
    }

    public Collection<Ban> getUsernameBans()
    {
        return Collections.unmodifiableCollection(nameBans.values());
    }

    public Ban getByIp(String ip)
    {
        final Ban directBan = ipBans.get(ip);
        if (directBan != null && !directBan.isExpired())
        {
            return directBan;
        }

        // Match fuzzy IP
        for (Ban loopBan : ipBans.values())
        {
            if (loopBan.isExpired())
            {
                continue;
            }

            for (String loopIp : loopBan.getIps())
            {
                if (!loopIp.contains("*"))
                {
                    continue;
                }

                if (FUtil.fuzzyIpMatch(ip, loopIp, 4))
                {
                    return loopBan;
                }
            }
        }

        return null;
    }

    public Ban getByUsername(String username)
    {
        username = username.toLowerCase();
        final Ban directBan = nameBans.get(username);

        if (directBan != null && !directBan.isExpired())
        {
            return directBan;
        }

        return null;
    }

    public Ban getByUUID(UUID uuid)
    {
        final Ban directBan = uuidBans.get(uuid);

        if (directBan != null && !directBan.isExpired())
        {
            return directBan;
        }

        return null;
    }

    public Ban unbanIp(String ip)
    {
        final Ban ban = getByIp(ip);

        if (ban != null)
        {
            bans.remove(ban);
        }

        return ban;
    }

    public Ban unbanUsername(String username)
    {
        final Ban ban = getByUsername(username);

        if (ban != null)
        {
            bans.remove(ban);
        }

        return ban;
    }

    public boolean isIpBanned(String ip)
    {
        return getByIp(ip) != null;
    }

    public boolean isUsernameBanned(String username)
    {
        return getByUsername(username) != null;
    }

    public boolean addBan(Ban ban)
    {
        if (ban.getUsername() != null && getByUsername(ban.getUsername()) != null)
        {
            removeBan(ban);
        }
        else
        {

            for (String ip : ban.getIps())
            {
                if (getByIp(ip) != null)
                {
                    removeBan(ban);
                    break;
                }
            }
        }

        if (bans.add(ban))
        {
            plugin.sql.addBan(ban);
            updateViews();
            return true;
        }

        return false;
    }

    public boolean removeBan(Ban ban)
    {
        if (bans.remove(ban))
        {
            plugin.sql.removeBan(ban);
            updateViews();
            return true;
        }

        return false;
    }

    public int purge()
    {
        int size = bans.size();
        bans.clear();
        updateViews();
        plugin.sql.truncate("bans");
        return size;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final String username = event.getPlayer().getName();
        final UUID uuid = event.getPlayer().getUniqueId();
        final String ip = FUtil.getIp(event);

        // Regular ban
        Ban ban = getByUsername(username);
        if (ban == null)
        {
            ban = getByUUID(uuid);

            if (ban == null)
            {
                ban = getByIp(ip);
            }
        }

        if (ban != null && !ban.isExpired())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ban.bakeKickMessage());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final PlayerData data = plugin.pl.getData(player);

        if (!plugin.sl.isStaff(player))
        {
            return;
        }

        // Unban admins
        Ban ban = getByUsername(player.getName());
        if (ban != null)
        {
            removeBan(ban);
        }
        else
        {
            ban = getByIp(FUtil.getIp(player));
            if (ban != null)
            {
                removeBan(ban);
            }
        }
    }

    private void updateViews()
    {
        // Remove expired bans
        for (Ban ban : new ArrayList<>(bans))
        {
            if (ban.isExpired())
            {
                bans.remove(ban);
                plugin.sql.removeBan(ban);
            }
        }

        nameBans.clear();
        uuidBans.clear();
        ipBans.clear();
        for (Ban ban : bans)
        {
            if (ban.hasUsername())
            {
                nameBans.put(ban.getUsername().toLowerCase(), ban);
            }

            if (ban.hasUUID())
            {
                uuidBans.put(ban.getUuid(), ban);
            }

            if (ban.hasIps())
            {
                for (String ip : ban.getIps())
                {
                    ipBans.put(ip, ban);
                }
            }
        }
    }

}
