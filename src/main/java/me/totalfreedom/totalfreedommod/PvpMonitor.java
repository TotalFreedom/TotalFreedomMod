package me.totalfreedom.totalfreedommod;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;



public class PvpMonitor extends FreedomService {

    public PvpMonitor(TotalFreedomMod plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(priority = EventPriority.LOW)


    public void hit(final EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        // This checks if the player is actually hitting a other player with any item , it filters his gamemode.
        if (damager instanceof Player && entity instanceof Player) {
            final Player player = (Player) damager;

            // Bypasses the block if Player is actually a Supered-Admin.
            if (plugin.al.isAdmin((player))) {
                return;
            }
            // Checks 4 cases
            if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) { // This checks if player is on creative and god mode on.
                player.sendMessage(ChatColor.RED + "Hey! You cannot PVP with God Mode and creative!");
                event.setCancelled(true);
            } else if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) { // This checks if player is on creative and god mode off.
                player.sendMessage(ChatColor.RED + "Hey! You cannot PVP in creative!");
                event.setCancelled(true);
            } else if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) { // This checks if player is on survival with  god mode on.
                player.sendMessage(ChatColor.RED + "Hey! You can't PVP with godmode!");
                event.setCancelled(true);
            } else if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) { // This checks if player is on Adventure with god mode on.
                player.sendMessage(ChatColor.RED + "Hey! You can't PVP with godmode!");
                event.setCancelled(true);
            }
        }

        // IF player shoots an arrow this prevents the damage if player is on creative or with godmode on.
        if (damager instanceof Projectile && entity instanceof Player) {

            ProjectileSource ps = ((Projectile) damager).getShooter();

            Player player = (Player) ps;

            // Bypasses the block if Player is actually a Supered-Admin.
            if (plugin.al.isAdmin((player))) {
                return;
            }

            if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) { // This checks if player is on creative and god mode on.
                player.sendMessage(ChatColor.RED + "Hey! You cannot PVP with God Mode and creative!");
                event.setCancelled(true);
            } else if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) { // This checks if player is on creative and god mode off.
                player.sendMessage(ChatColor.RED + "Hey! You cannot PVP in creative!");
                event.setCancelled(true);
            } else if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) { // This checks if player is on survival with  god mode on.
                player.sendMessage(ChatColor.RED + "Hey! You can't PVP with godmode!");
                event.setCancelled(true);
            } else if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) { // This checks if player is on Adventure with god mode on.
                player.sendMessage(ChatColor.RED + "Hey! You can't PVP with godmode!");
                event.setCancelled(true);
            }
        }
    }
}



