package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class Fuckoff extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        final Player fuckoffPlayer = event.getPlayer();
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
                fuckoffPlayer.setVelocity(onlinePlayer.getLocation().toVector().add(foLocation.toVector()).normalize().multiply(fPlayer.getFuckoffRadius()));
                break;
            }
        }
    }
}