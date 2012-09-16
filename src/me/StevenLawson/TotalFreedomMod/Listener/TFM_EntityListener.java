package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class TFM_EntityListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (!TotalFreedomMod.allowExplosions)
        {
            event.setCancelled(true);
            return;
        }

        event.setYield(0.0F);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        if (!TotalFreedomMod.allowExplosions)
        {
            event.setCancelled(true);
            return;
        }

        event.setRadius((float) TotalFreedomMod.explosiveRadius);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityCombust(EntityCombustEvent event)
    {
        if (!TotalFreedomMod.allowFireSpread)
        {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event)
    {
        switch (event.getCause())
        {
            case LAVA:
            {
                if (!TotalFreedomMod.allowLavaDamage)
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (TotalFreedomMod.mobLimiterEnabled)
        {
            Entity spawned = event.getEntity();

            if (spawned instanceof EnderDragon)
            {
                if (TotalFreedomMod.mobLimiterDisableDragon)
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Ghast)
            {
                if (TotalFreedomMod.mobLimiterDisableGhast)
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Slime)
            {
                if (TotalFreedomMod.mobLimiterDisableSlime)
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Giant)
            {
                if (TotalFreedomMod.mobLimiterDisableGiant)
                {
                    event.setCancelled(true);
                    return;
                }
            }

            if (TotalFreedomMod.mobLimiterMax > 0)
            {
                int mobcount = 0;

                for (Entity ent : event.getLocation().getWorld().getLivingEntities())
                {
                    if (ent instanceof Creature || ent instanceof Ghast || ent instanceof Slime || ent instanceof EnderDragon)
                    {
                        mobcount++;
                    }
                }

                if (mobcount > TotalFreedomMod.mobLimiterMax)
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (TotalFreedomMod.autoEntityWipe)
        {
            event.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(ProjectileHitEvent event)
    {
        if (TotalFreedomMod.allowExplosions)
        {
            Projectile entity = event.getEntity();
            if (event.getEntityType() == EntityType.ARROW && entity.getShooter() instanceof Player)
            {
                entity.getWorld().createExplosion(entity.getLocation(), 2F);
            }
        }
    }
}
