package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Orbiter extends FreedomService
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

        final Player player = event.getPlayer();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);

        if (!fPlayer.isOrbiting())
        {
            return;
        }

        if (player.getVelocity().length() < fPlayer.orbitStrength() * (2.0 / 3.0))
        {
            player.setVelocity(new Vector(0, fPlayer.orbitStrength(), 0));
        }
    }

}
