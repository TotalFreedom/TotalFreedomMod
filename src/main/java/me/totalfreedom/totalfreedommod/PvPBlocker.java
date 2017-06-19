package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FSync;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class PvPBlocker extends FreedomService
{

    public PvPBlocker(TotalFreedomMod plugin)
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
        Entity damager = event.getDamager();
        FPlayer fPlayer = null;
        if (damager instanceof Player)
        {
            fPlayer = plugin.pl.getPlayerSync((Player) damager);
        }

        if (damager instanceof Projectile)
        {
            ProjectileSource ps = ((Projectile) damager).getShooter();
            if (ps instanceof Player)
            {
                fPlayer = plugin.pl.getPlayerSync((Player) ps);
            }
        }

        if (fPlayer == null || !fPlayer.isPvpBlocked())
        {
            return;
        }

        if (plugin.al.isAdminSync(event.getDamager()))
        {
            fPlayer.setPvpBlocked(false);
            return;
        }

        Player player = (Player) damager;
        event.setCancelled(true);
        FSync.playerMsg(player, ChatColor.RED + "You have been disallowed from PvPing!");
    }

}
