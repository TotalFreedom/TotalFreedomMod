package me.unraveledmc.unraveledmcmod.blocking;

import me.unraveledmc.unraveledmcmod.FreedomService;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobBlocker extends FreedomService
{

    public MobBlocker(UnraveledMCMod plugin)
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (!ConfigEntry.MOB_LIMITER_ENABLED.getBoolean())
        {
            return;
        }

        final Entity spawned = event.getEntity();
        if (spawned instanceof EnderDragon)
        {
            if (ConfigEntry.MOB_LIMITER_DISABLE_DRAGON.getBoolean())
            {
                event.setCancelled(true);
                return;
            }
        }
        else if (spawned instanceof Ghast)
        {
            if (ConfigEntry.MOB_LIMITER_DISABLE_GHAST.getBoolean())
            {
                event.setCancelled(true);
                return;
            }
        }
        else if (spawned instanceof Slime)
        {
            if (ConfigEntry.MOB_LIMITER_DISABLE_SLIME.getBoolean())
            {
                event.setCancelled(true);
                return;
            }
        }
        else if (spawned instanceof Giant)
        {
            if (ConfigEntry.MOB_LIMITER_DISABLE_GIANT.getBoolean())
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

        int mobLimiterMax = ConfigEntry.MOB_LIMITER_MAX.getInteger();

        if (mobLimiterMax <= 0)
        {
            return;
        }

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
