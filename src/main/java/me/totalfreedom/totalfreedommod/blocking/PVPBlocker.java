package me.totalfreedom.totalfreedommod.blocking;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FSync;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        Entity damager = event.getDamager();
        FPlayer fPlayer = null;
        if (damager instanceof Player)
        {
            fPlayer = plugin.pl.getPlayerSync((Player) damager);
        }

        if (damager instanceof Projectile)
        {
            ProjectileSource projectileSource = ((Projectile) damager).getShooter();
            if (projectileSource instanceof Player)
            {
                fPlayer = plugin.pl.getPlayerSync((Player) projectileSource);
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
        FSync.playerMsg(player, ChatColor.RED + "You are forbidden to engage in PVP combat.");
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerAttack(final EntityDamageByEntityEvent event)
    {
        final Entity damager = event.getDamager();
        final Entity entity = event.getEntity();
        if (damager instanceof Player && entity instanceof Player)
        {
            final Player player = (Player) damager;

            if (plugin.al.isAdmin((player)))
            {
                return;
            }
            if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled())
            {
                player.sendMessage(ChatColor.RED + "Hey! You cannot PVP with God Mode and creative!");
                event.setCancelled(true);
            }
            else if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled())
            {
                player.sendMessage(ChatColor.RED + "Hey! You cannot PVP in creative!");
                event.setCancelled(true);
            }
            else if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled())
            {
                player.sendMessage(ChatColor.RED + "Hey! You can't PVP with godmode!");
                event.setCancelled(true);
            }
            else if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled())
            {
                player.sendMessage(ChatColor.RED + "Hey! You can't PVP with godmode!");
                event.setCancelled(true);
            }
        }

        if (damager instanceof Projectile && entity instanceof Player)
        {
            ProjectileSource projectileSource = ((Projectile) damager).getShooter();

            Player player = (Player) projectileSource;

            if (plugin.al.isAdmin((player)))
            {
                return;
            }

            if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled())
            {
                player.sendMessage(ChatColor.RED + "Hey! You cannot PVP with God Mode and creative!");
                event.setCancelled(true);
            }
            else if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled())
            {
                player.sendMessage(ChatColor.RED + "Hey! You cannot PVP in creative!");
                event.setCancelled(true);
            }
            else if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled())
            {
                player.sendMessage(ChatColor.RED + "Hey! You can't PVP with godmode!");
                event.setCancelled(true);
            }
            else if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled())
            {
                player.sendMessage(ChatColor.RED + "Hey! You can't PVP with godmode!");
                event.setCancelled(true);
            }
        }
    }

}
