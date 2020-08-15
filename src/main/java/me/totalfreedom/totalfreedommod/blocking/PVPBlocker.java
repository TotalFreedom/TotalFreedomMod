package me.totalfreedom.totalfreedommod.blocking;

import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PVPBlocker extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        Player player = null;
        Player target = null;
        if (event.getEntity() instanceof Player)
        {
            target = (Player)event.getEntity();
            if (event.getDamager() instanceof Player)
            {
                player = (Player)event.getDamager();
            }
            else if (event.getDamager() instanceof Arrow)
            {
                Arrow arrow = (Arrow)event.getDamager();
                if (arrow.getShooter() instanceof Player)
                {
                    player = (Player)arrow.getShooter();
                }
            }
            else if (event.getDamager() instanceof Trident)
            {
                Trident trident = (Trident)event.getDamager();
                if (trident.getShooter() instanceof Player)
                {
                    player = (Player)trident.getShooter();
                }
            }
            else if (event.getDamager() instanceof FishHook)
            {
                FishHook fishhook = (FishHook)event.getDamager();
                if (fishhook.getShooter() instanceof Player)
                {
                    player = (Player)fishhook.getShooter();
                }
            }
        }

        if (player != null & !plugin.sl.isStaff(player))
        {
            if (player.getGameMode() == GameMode.CREATIVE)
            {
                player.sendMessage(ChatColor.RED + "Creative PvP is not allowed!");
                event.setCancelled(true);
            }
            else if (plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled())
            {
                player.sendMessage(ChatColor.RED + "God mode PvP is not allowed!");
                event.setCancelled(true);
            }
            else if (plugin.pl.getPlayer(target).isPvpBlocked())
            {
                player.sendMessage(ChatColor.RED + target.getName() + " has PvP disabled!");
                event.setCancelled(true);
            }
            else if (plugin.pl.getPlayer(player).isPvpBlocked())
            {
                player.sendMessage(ChatColor.RED + "You have PvP disabled!");
                event.setCancelled(true);
            }
        }

    }

}
