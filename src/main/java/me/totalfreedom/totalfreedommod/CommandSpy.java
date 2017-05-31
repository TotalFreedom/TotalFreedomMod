package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CommandSpy extends FreedomService
{

    public CommandSpy(TotalFreedomMod plugin)
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
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        if (plugin.al.isAdmin(event.getPlayer()))
        {
            return;
        }

        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).cmdspyEnabled())
            {
                FUtil.playerMsg(player, event.getPlayer().getName() + ": " + event.getMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Admin admin = getAdmin((Player) event.getPlayer());
        FPlayer playerdata = plugin.pl.getPlayer(event.getPlayer());

        if (plugin.al.isAdmin(event.getPlayer()))
        {
            if (admin.hasCommandSpy())
            {

                playerdata.setCommandSpy(playerdata.cmdspyEnabled());
            }
            else
            {
                playerdata.setCommandSpy(playerdata.cmdspyEnabled() == false);
            }
        }
        else
        {
            playerdata.setCommandSpy(playerdata.cmdspyEnabled() == false);
        }
    }

}
