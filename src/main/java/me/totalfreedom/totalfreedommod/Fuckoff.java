package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class Fuckoff extends FreedomService
{

    public Fuckoff(TotalFreedomMod plugin)
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
    public void onPlayerMove(PlayerMoveEvent event)
    {
        final Player fuckoffPlayer = event.getPlayer();
        if (plugin.al.isAdmin(fuckoffPlayer))
        {
            return;
        }

        for (Player onlinePlayer : server.getOnlinePlayers())
        {
            final FPlayer fPlayer = plugin.pl.getPlayer(onlinePlayer);
            if (!fPlayer.isFuckOff()
                    || fuckoffPlayer.equals(onlinePlayer))
            {
                continue;
            }

            double fuckoffRange = fPlayer.getFuckoffRadius();
            Location opLocation = onlinePlayer.getLocation();
            Location foLocation = fuckoffPlayer.getLocation();

            double distanceSquared;
            try
            {
                distanceSquared = opLocation.distanceSquared(foLocation);
            }
            catch (IllegalArgumentException ex)
            {
                continue;
            }

            if (distanceSquared < (fuckoffRange * fuckoffRange))
            {
                event.setTo(foLocation.clone().add(opLocation.subtract(foLocation).toVector().normalize().multiply(fuckoffRange * 1.1)));
                break;
            }
        }
    }

}
