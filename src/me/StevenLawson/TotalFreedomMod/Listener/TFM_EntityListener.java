package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class TFM_EntityListener extends EntityListener
{
    private TotalFreedomMod plugin;

    public TFM_EntityListener(TotalFreedomMod instance)
    {
        this.plugin = instance;
    }

    @Override
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (!TotalFreedomMod.allowExplosions)
        {
            event.setCancelled(true);
            return;
        }

        event.setYield(0.0f);
    }

    @Override
    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        if (!TotalFreedomMod.allowExplosions)
        {
            event.setCancelled(true);
            return;
        }

        event.setRadius((float) TotalFreedomMod.explosiveRadius);
    }

    @Override
    public void onEntityCombust(EntityCombustEvent event)
    {
        if (!TotalFreedomMod.allowFireSpread)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.getCause() == DamageCause.LAVA && !TotalFreedomMod.allowLavaDamage)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
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

                for (World world : Bukkit.getWorlds())
                {
                    for (Entity ent : world.getLivingEntities())
                    {
                        if (ent instanceof Creature || ent instanceof Ghast || ent instanceof Slime || ent instanceof EnderDragon)
                        {
                            mobcount++;
                        }
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
}
