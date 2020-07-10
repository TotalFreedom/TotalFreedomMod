package me.totalfreedom.totalfreedommod;

import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EntityWiper extends FreedomService
{
    private BukkitTask wiper;

    public List<EntityType> BLACKLIST = Arrays.asList(
            EntityType.ARMOR_STAND,
            EntityType.PAINTING,
            EntityType.BOAT,
            EntityType.LEASH_HITCH,
            EntityType.ITEM_FRAME,
            EntityType.MINECART
    );

    @Override
    public void onStart()
    {
        // Continuous Entity Wiper
        wiper = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                wipeEntities(false);
            }
        }.runTaskTimer(plugin, 600L, 600L); // 30 second delay after startup + run every 30 seconds
    }

    @Override
    public void onStop()
    {
        wiper.cancel();
        wiper = null;
    }

    // Methods for wiping

    public int wipeEntities(boolean bypassBlacklist)
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity entity : world.getEntities())
            {
                if (!(entity instanceof Player))
                {
                    if ((!bypassBlacklist && BLACKLIST.contains(entity.getType())) || Groups.MOB_TYPES.contains(entity.getType()))
                    {
                        continue;
                    }
                    entity.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

    public int wipeEntities(EntityType entityType)
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity entity : world.getEntities())
            {
                if (!entity.getType().equals(entityType))
                {
                    continue;
                }
                entity.remove();
                removed++;
            }
        }
        return removed;
    }

    public int purgeMobs(EntityType type)
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity entity : world.getLivingEntities())
            {
                if (entity instanceof LivingEntity && !(entity instanceof Player))
                {
                    if (type != null && !entity.getType().equals(type))
                    {
                        continue;
                    }
                    entity.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

}
