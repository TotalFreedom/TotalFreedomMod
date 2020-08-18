package me.totalfreedom.totalfreedommod.world;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WorldRestrictions extends FreedomService
{

    private final List<String> BLOCKED_WORLDEDIT_COMMANDS = Arrays.asList(
            "green", "fixlava", "fixwater", "br", "brush", "tool", "mat", "range", "cs", "up", "fill", "setblock", "tree", "replacenear", "bigtree");

    private final Map<Flag<?>, Object> flags = new HashMap<Flag<?>, Object>()
    {{
        put(Flags.PLACE_VEHICLE, StateFlag.State.DENY);
        put(Flags.DESTROY_VEHICLE, StateFlag.State.DENY);
        put(Flags.ENTITY_ITEM_FRAME_DESTROY, StateFlag.State.DENY);
        put(Flags.ENTITY_PAINTING_DESTROY, StateFlag.State.DENY);
        put(net.goldtreeservers.worldguardextraflags.flags.Flags.WORLDEDIT, StateFlag.State.DENY);
    }};

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public boolean doRestrict(Player player)
    {
        if (!plugin.pl.getData(player).isMasterBuilder() && !plugin.pl.canManageMasterBuilders(player.getName()))
        {
            if (player.getWorld().equals(plugin.wm.masterBuilderWorld.getWorld()) || player.getWorld().equals(plugin.wm.hubworld.getWorld()))
            {
                return true;
            }
        }

        if (!plugin.sl.isStaff(player) && player.getWorld().equals(plugin.wm.staffworld.getWorld()))
        {
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player)event.getDamager();

            if (doRestrict(player))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        final Player player = event.getPlayer();
        if (doRestrict(player))
        {
            /* This is a very poor way of blocking WorldEdit commands, all the methods I know of
               for obtaining a list of a plugin's commands are returning null for world edit. */
            String command = event.getMessage().split("\\s+")[0].substring(1, event.getMessage().split("\\s+")[0].length()).toLowerCase();

            String allowed = player.getWorld().equals(plugin.wm.staffworld.getWorld()) ? "Staff" : "Master Builders";

            if (command.startsWith("/") || BLOCKED_WORLDEDIT_COMMANDS.contains(command))
            {
                player.sendMessage(ChatColor.RED + "Only " + allowed + " are allowed to use WorldEdit here.");
                event.setCancelled(true);
            }

            if (command.equals("coreprotect") || command.equals("core") || command.equals("co"))
            {
                player.sendMessage(ChatColor.RED + "Only " + allowed + " are allowed to use CoreProtect here.");
                event.setCancelled(true);
            }
        }
    }


    public void protectWorld(World world)
    {
        if (!plugin.wgb.isEnabled())
        {
            return;
        }

        RegionManager regionManager = plugin.wgb.getRegionManager(world);

        GlobalProtectedRegion region = new GlobalProtectedRegion("__global__");

        region.setFlags(flags);

        regionManager.addRegion(region);
    }
}
