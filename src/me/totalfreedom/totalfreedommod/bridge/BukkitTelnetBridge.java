package me.totalfreedom.totalfreedommod.bridge;

import java.util.Iterator;
import java.util.Map;
import me.StevenLawson.BukkitTelnet.api.TelnetCommandEvent;
import me.StevenLawson.BukkitTelnet.api.TelnetPreLoginEvent;
import me.StevenLawson.BukkitTelnet.api.TelnetRequestDataTagsEvent;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.pravian.aero.component.service.AbstractService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class BukkitTelnetBridge extends AbstractService<TotalFreedomMod>
{

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

        final Admin admin = plugin.al.getEntryByIp(ip, true);

        if (admin == null || !admin.isActivated() || !admin.getRank().hasConsole())
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
                boolean isActivated = admin.isActivated();

                isAdmin = isActivated;
                isSeniorAdmin = isActivated && admin.getRank() == PlayerRank.SENIOR_ADMIN;
                isTelnetAdmin = isActivated && (isSeniorAdmin || admin.getRank() == PlayerRank.TELNET_ADMIN);
            }

            playerTags.put("tfm.admin.isAdmin", isAdmin);
            playerTags.put("tfm.admin.isTelnetAdmin", isTelnetAdmin);
            playerTags.put("tfm.admin.isSeniorAdmin", isSeniorAdmin);

            playerTags.put("tfm.playerdata.getTag", plugin.pl.getPlayer(player).getTag());

            playerTags.put("tfm.essentialsBridge.getNickname", plugin.esb.getNickname(player.getName()));
        }
    }
}
