package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class Vanish extends FreedomService
{

    public Vanish(TotalFreedomMod plugin)
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
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        for (Player vanishedPlayers : server.getOnlinePlayers())
        {
            final FPlayer fPlayer = plugin.pl.getPlayer(vanishedPlayers);
            if (fPlayer.isVanish())
            {
                player.hidePlayer(vanishedPlayers);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);
        if (fPlayer.isVanish())
        {

            FLog.info(player.getName() + " is no longer vanished.");
            for (Player allPlayers : server.getOnlinePlayers())
            {
                player.showPlayer(allPlayers);
            }
            plugin.esb.setVanished(player.getName(), false);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            fPlayer.setVanish(false);
        }
    }

}
