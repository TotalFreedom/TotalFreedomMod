package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FSync;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class PVPBlocker extends FreedomService
{

    public PVPBlocker(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.LOW)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event)
    {
        FPlayer fPlayer = null;

        if (event.getDamager() instanceof Player)
        {
            fPlayer = plugin.pl.getPlayerSync((Player) event.getDamager());
        }
        else if (event.getDamager() instanceof Arrow)
        {
            ProjectileSource ps = ((Arrow) event.getDamager()).getShooter();

            if (ps instanceof Player)
            {
                fPlayer = plugin.pl.getPlayerSync((Player) ps);
            }
            else
            {
                return;
            }
        }

        if (fPlayer == null)
        {
            return;
        }

        if (!fPlayer.isPVPBlock())
        {
            return;
        }

        if (plugin.al.isAdminSync(event.getDamager()))
        {
            fPlayer.setPVPBlock(false);
            return;
        }

        Entity p = event.getEntity();
        Entity offense = event.getDamager();
        if (p instanceof Player)
        {
            event.setCancelled(true);
        }
        if (offense instanceof Player)
        {
            event.setCancelled(true);
            FSync.playerMsg((Player) event.getDamager(), ChatColor.RED + "Your PVP mode is blocked!");
            event.setCancelled(true);
            event.isCancelled();
        }
    }
}
