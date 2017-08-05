package me.totalfreedom.totalfreedommod.bridge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.totalfreedom.bukkittelnet.BukkitTelnet;
import me.totalfreedom.bukkittelnet.api.TelnetCommandEvent;
import me.totalfreedom.bukkittelnet.api.TelnetPreLoginEvent;
import me.totalfreedom.bukkittelnet.api.TelnetRequestDataTagsEvent;
import me.totalfreedom.bukkittelnet.session.ClientSession;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

public class BukkitTelnetBridge extends FreedomService
{

    private BukkitTelnet bukkitTelnetPlugin = null;

    public BukkitTelnetBridge(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTelnetPreLogin(TelnetPreLoginEvent event)
    {

        final String ip = event.getIp();
        if (ip == null || ip.isEmpty())
        {
            return;
        }

        final Admin admin = plugin.al.getEntryByIpFuzzy(ip);

        if (admin == null || !admin.isActive() || !admin.getRank().hasConsoleVariant())
        {
            return;
        }

        event.setBypassPassword(true);
        event.setName(admin.getName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTelnetCommand(TelnetCommandEvent event)
    {
        if (plugin.cb.isCommandBlocked(event.getCommand(), event.getSender()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTelnetRequestDataTags(TelnetRequestDataTagsEvent event)
    {
        final Iterator<Map.Entry<Player, Map<String, Object>>> it = event.getDataTags().entrySet().iterator();
        while (it.hasNext())
        {
            final Map.Entry<Player, Map<String, Object>> entry = it.next();
            final Player player = entry.getKey();
            final Map<String, Object> playerTags = entry.getValue();

            boolean isAdmin = false;
            boolean isTelnetAdmin = false;
            boolean isSeniorAdmin = false;

            final Admin admin = plugin.al.getAdmin(player);
            if (admin != null)
            {
                boolean active = admin.isActive();

                isAdmin = active;
                isSeniorAdmin = active && admin.getRank() == Rank.SENIOR_ADMIN;
                isTelnetAdmin = active && (isSeniorAdmin || admin.getRank() == Rank.TELNET_ADMIN);
            }

            playerTags.put("tfm.admin.isAdmin", isAdmin);
            playerTags.put("tfm.admin.isTelnetAdmin", isTelnetAdmin);
            playerTags.put("tfm.admin.isSeniorAdmin", isSeniorAdmin);

            playerTags.put("tfm.playerdata.getTag", plugin.pl.getPlayer(player).getTag());

            playerTags.put("tfm.essentialsBridge.getNickname", plugin.esb.getNickname(player.getName()));
        }
    }

    public BukkitTelnet getBukkitTelnetPlugin()
    {
        if (bukkitTelnetPlugin == null)
        {
            try
            {
                final Plugin bukkitTelnet = server.getPluginManager().getPlugin("BukkitTelnet");
                if (bukkitTelnet != null)
                {
                    if (bukkitTelnet instanceof BukkitTelnet)
                    {
                        bukkitTelnetPlugin = (BukkitTelnet) bukkitTelnet;
                    }
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }

        return bukkitTelnetPlugin;
    }

    public void killTelnetSessions(final String name)
    {
        try
        {
            final List<ClientSession> sessionsToRemove = new ArrayList<>();

            final BukkitTelnet telnet = getBukkitTelnetPlugin();
            if (telnet != null)
            {
                final Iterator<ClientSession> it = telnet.appender.getSessions().iterator();
                while (it.hasNext())
                {
                    final ClientSession session = it.next();
                    if (name != null && name.equalsIgnoreCase(session.getUserName()))
                    {
                        sessionsToRemove.add(session);
                    }
                }

                for (final ClientSession session : sessionsToRemove)
                {
                    try
                    {
                        telnet.appender.removeSession(session);
                        session.syncTerminateSession();
                    }
                    catch (Exception ex)
                    {
                        FLog.severe("Error removing single telnet session: " + ex.getMessage());
                    }
                }

                FLog.info(sessionsToRemove.size() + " telnet session(s) removed.");
            }
        }
        catch (Exception ex)
        {
            FLog.severe("Error removing telnet sessions: " + ex.getMessage());
        }
    }
}
