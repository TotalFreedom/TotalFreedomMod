package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
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
        if (!TFM_ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            event.setCancelled(true);
            return;
        }

        event.setYield(0.0F);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        if (!TFM_ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            event.setCancelled(true);
            return;
        }

        event.setRadius((float) TFM_ConfigEntry.EXPLOSIVE_RADIUS.getDouble().doubleValue());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityCombust(EntityCombustEvent event)
    {
        if (!TFM_ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event)
    {
        switch (event.getCause())
        {
            case LAVA:
            {
                if (!TFM_ConfigEntry.ALLOW_LAVA_DAMAGE.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (TFM_ConfigEntry.ENABLE_PET_PROTECT.getBoolean())
        {
            Entity entity = event.getEntity();
            if (entity instanceof Tameable)
            {
                if (((Tameable) entity).isTamed())
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (TFM_ConfigEntry.MOB_LIMITER_ENABLED.getBoolean())
        {
            if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.EGG))
            {
                event.setCancelled(true);
                return;
            }

            Entity spawned = event.getEntity();

            if (spawned instanceof EnderDragon)
            {
                if (TFM_ConfigEntry.MOB_LIMITER_DISABLE_DRAGON.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Ghast)
            {
                if (TFM_ConfigEntry.MOB_LIMITER_DISABLE_GHAST.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Slime)
            {
                if (TFM_ConfigEntry.MOB_LIMITER_DISABLE_SLIME.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Giant)
            {
                if (TFM_ConfigEntry.MOB_LIMITER_DISABLE_GIANT.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Bat)
            {
                event.setCancelled(true);
                return;
            }

            int mobLimiterMax = TFM_ConfigEntry.MOB_LIMITER_MAX.getInteger().intValue();

            if (mobLimiterMax > 0)
            {
                int mobcount = 0;

                for (Entity entity : event.getLocation().getWorld().getLivingEntities())
                {
                    if (!(entity instanceof HumanEntity))
                    {
                        mobcount++;
                    }
                }

                if (mobcount > mobLimiterMax)
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (TFM_ConfigEntry.AUTO_ENTITY_WIPE.getBoolean())
        {
            event.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(ProjectileHitEvent event)
    {
        if (TFM_ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            Projectile entity = event.getEntity();
            if (event.getEntityType() == EntityType.ARROW)
            {
                entity.getWorld().createExplosion(entity.getLocation(), 2F);
            }
        }
    }
}
