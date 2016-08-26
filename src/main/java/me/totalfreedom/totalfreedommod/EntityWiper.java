package me.totalfreedom.totalfreedommod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EntityWiper extends FreedomService
{

    public static final long ENTITY_WIPE_RATE = 5 * 20L;
    public static final long ITEM_DESPAWN_RATE = 20L * 20L;
    public static final int CHUNK_ENTITY_MAX = 30;
    //
    private final List<Class<? extends Entity>> wipables = new ArrayList<>();
    //
    private BukkitTask wipeTask;

    public EntityWiper(TotalFreedomMod plugin)
    {
        super(plugin);
        wipables.add(EnderCrystal.class);
        wipables.add(EnderSignal.class);
        wipables.add(ExperienceOrb.class);
        wipables.add(Projectile.class);
        wipables.add(FallingBlock.class);
        wipables.add(Firework.class);
        wipables.add(Item.class);
        wipables.add(ThrownPotion.class);
        wipables.add(ThrownExpBottle.class);
        wipables.add(AreaEffectCloud.class);
        wipables.add(Minecart.class);
        wipables.add(Boat.class);
        wipables.add(FallingBlock.class);
    }

    @Override
    protected void onStart()
    {
        if (!ConfigEntry.AUTO_ENTITY_WIPE.getBoolean())
        {
            return;
        }

        wipeTask = new BukkitRunnable()
        {

            @Override
            public void run()
            {
                wipeEntities();
            }
        }.runTaskTimer(plugin, ENTITY_WIPE_RATE, ENTITY_WIPE_RATE);

    }

    @Override
    protected void onStop()
    {
        FUtil.cancel(wipeTask);
        wipeTask = null;
    }

    public boolean isWipeable(Entity entity)
    {
        for (Class<? extends Entity> c : wipables)
        {
            if (c.isAssignableFrom(entity.getClass()))
            {
                return true;
            }
        }

        return false;
    }

    public int wipeEntities()
    {
        int removed = 0;
        Iterator<World> worlds = Bukkit.getWorlds().iterator();
        while (worlds.hasNext())
        {
            removed += wipeEntities(worlds.next());
        }

        return removed;
    }

    public int wipeEntities(World world)
    {
        int removed = 0;

        boolean wipeExpl = ConfigEntry.ALLOW_EXPLOSIONS.getBoolean();
        Iterator<Entity> entities = world.getEntities().iterator();

        // Organise the entities in the world
        Map<Chunk, List<Entity>> cem = new HashMap<>();
        while (entities.hasNext())
        {
            final Entity entity = entities.next();

            // Explosives
            if (wipeExpl && Explosive.class.isAssignableFrom(entity.getClass()))
            {
                entity.remove();
                removed++;
            }

            // Only wipeable entities can be wiped (duh!)
            if (!isWipeable(entity))
            {
                continue;
            }

            Chunk c = entity.getLocation().getChunk();
            List<Entity> cel = cem.get(c);
            if (cel == null)
            {
                cem.put(c, new ArrayList<>(Arrays.asList(entity)));
            }
            else
            {
                cel.add(entity);
            }
        }

        // Now purge the entities if necessary
        for (Chunk c : cem.keySet())
        {
            List<Entity> cel = cem.get(c);

            if (cel.size() < CHUNK_ENTITY_MAX)
            {
                continue;
            }

            // Too many entities in this chunk, wipe them all
            for (Entity e : cel)
            {
                e.remove();
            }
        }

        return removed;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event)
    {
        final Item entity = event.getEntity();

        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                entity.remove();
            }
        }.runTaskLater(plugin, ITEM_DESPAWN_RATE);

    }

}
