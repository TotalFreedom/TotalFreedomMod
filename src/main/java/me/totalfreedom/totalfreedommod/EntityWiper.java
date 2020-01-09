package me.totalfreedom.totalfreedommod;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EntityWiper extends FreedomService
{
    private BukkitTask wiper;

    public EntityWiper(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        // Continuous Entity Wiper
        wiper = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (World world : Bukkit.getWorlds())
                {
                    if (world.getEntities().size() > 400)
                    {
                        world.getEntities().clear();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    protected void onStop()
    {
        wiper.cancel();
        wiper = null;
    }

    // Methods for wiping

    public int wipe()
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity entity : world.getEntities())
            {
                if (!(entity instanceof Player))
                {
                    entity.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

    public int wipe(World world)
    {
        int removed = 0;
        for (Entity entity : world.getEntities())
        {
            if (!(entity instanceof Player))
            {
                entity.remove();
                removed++;
            }
        }
        return removed;
    }
}