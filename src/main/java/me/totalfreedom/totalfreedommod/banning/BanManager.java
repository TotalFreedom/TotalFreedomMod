package me.totalfreedom.totalfreedommod.banning;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.component.service.AbstractService;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Ips;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class BanManager extends AbstractService<TotalFreedomMod>
{

    private final Set<Ban> bans = Sets.newHashSet();
    private final Map<String, Ban> ipBans = Maps.newHashMap();
    private final Map<String, Ban> nameBans = Maps.newHashMap();
    private final List<String> unbannableUsernames = Lists.newArrayList();
    //
    private final YamlConfig config;

    public BanManager(TotalFreedomMod plugin)
    {
        super(plugin);
        this.config = new YamlConfig(TotalFreedomMod.plugin, "bans.yml");
    }

    @Override
    protected void onStart()
    {
        load();
    }

    @Override
    protected void onStop()
    {
        saveAll();
    }

    public void load()
    {
        config.load();

        bans.clear();
        for (String id : config.getKeys(false))
        {
            if (!config.isConfigurationSection(id))
            {
                FLog.warning("Could not load username ban: " + id + ". Invalid format!");
                continue;
            }

            Ban ban = new Ban();
            ban.loadFrom(config.getConfigurationSection(id));

            if (!ban.isValid())
            {
                FLog.warning("Not adding username ban: " + id + ". Missing information.");
                continue;
            }

            bans.add(ban);
        }

        // Remove expired bans, repopulate ipBans and nameBans,
        updateViews();

        FLog.info("Loaded " + ipBans.size() + " IP bans and " + nameBans.size() + " username bans");

        // Load unbannable usernames
        unbannableUsernames.clear();
        unbannableUsernames.addAll((Collection<? extends String>) ConfigEntry.UNBANNABLE_USERNAMES.getList());
        FLog.info("Loaded " + unbannableUsernames.size() + " unbannable usernames.");

    }

    private void updateViews()
    {

        // Remove expired bans
        for (Iterator<Ban> it = bans.iterator(); it.hasNext();)
        {
            if (it.next().isExpired())
            {
                it.remove();
            }
        }

        ipBans.clear();
        nameBans.clear();
        for (Ban ban : bans)
        {
            if (ban.hasUsername())
            {
                nameBans.put(ban.getUsername().toLowerCase(), ban);
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

    public void saveAll()
    {
        // Remove expired
        updateViews();

        for (Ban ban : bans)
        {
            ban.saveTo(config.createSection(String.valueOf(ban.hashCode())));
        }

        // Save config
        config.save();
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

                if (Ips.fuzzyIpMatch(ip, loopIp, 4))
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

    public Ban unbanIp(String ip)
    {
        final Ban ban = getByIp(ip);

        if (ban == null)
        {
            return ban;
        }

        bans.remove(ban);
        saveAll();
        return ban;
    }

    public Ban unbanUsername(String username)
    {
        final Ban ban = getByUsername(username);

        if (ban == null)
        {
            return ban;
        }

        bans.remove(ban);
        saveAll();
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
        if (bans.add(ban))
        {
            updateViews();
            return true;
        }

        return false;
    }

    public boolean removeBan(Ban ban)
    {
        if (bans.remove(ban))
        {
            updateViews();
            return true;
        }

        return false;
    }

    public void purgeIpBans()
    {
        ipBans.clear();
        saveAll();
    }

    public void purgeNameBans()
    {
        nameBans.clear();
        saveAll();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final String username = event.getPlayer().getName();
        final String ip = Ips.getIp(event);

        // Regular ban
        Ban ban = plugin.bm.getByUsername(username);
        if (ban == null)
        {
            ban = plugin.bm.getByIp(ip);
        }

        if (ban != null)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ban.bakeKickMessage());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final PlayerData data = plugin.pl.getData(player);

        if (!plugin.al.isAdmin(player))
        {
            return;
        }

        // Unban admins
        for (String storedIp : data.getIps())
        {
            unbanIp(storedIp);
            unbanIp(FUtil.getFuzzyIp(storedIp));
        }

        unbanUsername(player.getName());
        player.setOp(true);
    }

}
