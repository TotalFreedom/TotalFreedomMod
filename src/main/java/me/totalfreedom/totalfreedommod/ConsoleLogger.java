package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.component.service.AbstractService;
import net.pravian.aero.util.Ips;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConsoleLogger extends AbstractService<TotalFreedomMod>
{

    public ConsoleLogger(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        FLog.info("[EXIT] " + event.getPlayer().getName() + " left the game.", true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final String ip = Ips.getIp(event.getPlayer());
        FLog.info("[JOIN] " + FUtil.formatPlayer(event.getPlayer()) + " joined the game with IP address: " + ip, true);
    }

}
