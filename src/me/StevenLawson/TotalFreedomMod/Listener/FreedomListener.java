package me.StevenLawson.TotalFreedomMod.Listener;

import java.util.Collection;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

// Implement FreedomOp Remastered methods
public class FreedomListener implements Listener
{

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event)
    {
        if (event.getReason().equals("You logged in from another location") && TFM_AdminList.isSuperAdmin(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (event.getFrom() == null || event.getTo() == null)
        {
            return;
        }

        Player player = event.getPlayer();

        if (event.getTo().getBlockX() >= 29999000 || event.getTo().getBlockZ() >= 29999000)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerConsumePotion(PlayerItemConsumeEvent event)
    {
        if (event.getItem().getType() == Material.POTION)
        {
            Collection<PotionEffect> fx = Potion.fromItemStack(event.getItem()).getEffects();
            for (PotionEffect effect : fx)
            {
                if (effect.getType() == PotionEffectType.INVISIBILITY && !TFM_AdminList.isSuperAdmin(event.getPlayer()))
                {
                    event.getPlayer().sendMessage(ChatColor.RED + "Invisibility is not allowed.");
                    event.setCancelled(true);
                }
                if (effect.getAmplifier() < 0)
                {
                    event.getPlayer().sendMessage(ChatColor.RED + "Effects with a negative amplifier are not allowed.");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.EGG))
        {
            event.setCancelled(true);
            return;
        }

        Entity spawned = event.getEntity();

        if (spawned instanceof EnderDragon)
        {
            event.setCancelled(true);
        }
        else if (spawned instanceof Ghast)
        {
            event.setCancelled(true);
        }
        else if (spawned instanceof Slime)
        {
            event.setCancelled(true);
        }
        else if (spawned instanceof Giant)
        {
            event.setCancelled(true);
        }
        else if (spawned instanceof Wither)
        {
            event.setCancelled(true);
        }
    }
}
