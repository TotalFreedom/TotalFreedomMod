package me.totalfreedom.totalfreedommod.caging;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.component.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Cager extends AbstractService<TotalFreedomMod>
{

    public Cager(TotalFreedomMod plugin)
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        FPlayer player = plugin.pl.getPlayer(event.getPlayer());
        CageData cage = player.getCageData();

        if (!cage.isCaged())
        {
            return;
        }

        Location playerLoc = player.getPlayer().getLocation().add(0, 1, 0);
        Location cageLoc = cage.getLocation();

        final boolean outOfCage;
        if (!playerLoc.getWorld().equals(cageLoc.getWorld()))
        {
            outOfCage = true;
        }
        else
        {
            outOfCage = playerLoc.distanceSquared(cageLoc) > (2.5D * 2.5D);
        }

        if (outOfCage)
        {
            player.getPlayer().teleport(cageLoc.subtract(0, 0.5, 0));
            FUtil.playerMsg(player.getPlayer(), "You may not leave your cage.", ChatColor.RED);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        FPlayer player = plugin.pl.getPlayer(event.getPlayer());
        CageData cage = player.getCageData();

        if (cage.isCaged())
        {
            cage.playerQuit();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        FPlayer player = plugin.pl.getPlayer(event.getPlayer());
        CageData cage = player.getCageData();

        if (cage.isCaged())
        {
            cage.playerJoin();
        }
    }

}
