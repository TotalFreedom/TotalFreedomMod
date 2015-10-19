package me.totalfreedom.totalfreedommod.listener;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.pravian.aero.component.PluginListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener extends PluginListener<TotalFreedomMod>
{

    public BlockListener(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBurn(BlockBurnEvent event)
    {
        if (!ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        if (!ConfigEntry.ALLOW_FIRE_PLACE.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
        {
            final FPlayer playerdata = plugin.pl.getPlayer(player);

            final Location playerLocation = player.getLocation();

            final double nukeMonitorRange = ConfigEntry.NUKE_MONITOR_RANGE.getDouble().doubleValue();

            boolean outOfRange = false;
            if (!playerLocation.getWorld().equals(location.getWorld()))
            {
                outOfRange = true;
            }
            else if (playerLocation.distanceSquared(location) > (nukeMonitorRange * nukeMonitorRange))
            {
                outOfRange = true;
            }

            if (outOfRange)
            {
                if (playerdata.incrementAndGetFreecamDestroyCount() > ConfigEntry.FREECAM_TRIGGER_COUNT.getInteger())
                {
                    FUtil.bcastMsg(player.getName() + " has been flagged for possible freecam nuking.", ChatColor.RED);
                    FUtil.autoEject(player, "Freecam (extended range) block breaking is not permitted on this server.");

                    playerdata.resetFreecamDestroyCount();

                    event.setCancelled(true);
                    return;
                }
            }

            final Long lastRan = plugin.hb.getLastRan();
            if (lastRan == null || lastRan + TotalFreedomMod.HEARTBEAT_RATE * 1000L < System.currentTimeMillis())
            {
                // TFM_Log.warning("Heartbeat service timeout - can't check block place/break rates.");
            }
            else
            {
                if (playerdata.incrementAndGetBlockDestroyCount() > ConfigEntry.NUKE_MONITOR_COUNT_BREAK.getInteger())
                {
                    FUtil.bcastMsg(player.getName() + " is breaking blocks too fast!", ChatColor.RED);
                    FUtil.autoEject(player, "You are breaking blocks too fast. Nukers are not permitted on this server.");

                    playerdata.resetBlockDestroyCount();

                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (ConfigEntry.PROTECTAREA_ENABLED.getBoolean())
        {
            if (!plugin.al.isAdmin(player))
            {
                if (plugin.pa.isInProtectedArea(location))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        Location blockLocation = event.getBlock().getLocation();

        if (ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
        {
            FPlayer playerdata = plugin.pl.getPlayer(player);

            Location playerLocation = player.getLocation();

            double nukeMonitorRange = ConfigEntry.NUKE_MONITOR_RANGE.getDouble().doubleValue();

            boolean outOfRange = false;
            if (!playerLocation.getWorld().equals(blockLocation.getWorld()))
            {
                outOfRange = true;
            }
            else if (playerLocation.distanceSquared(blockLocation) > (nukeMonitorRange * nukeMonitorRange))
            {
                outOfRange = true;
            }

            if (outOfRange)
            {
                if (playerdata.incrementAndGetFreecamPlaceCount() > ConfigEntry.FREECAM_TRIGGER_COUNT.getInteger())
                {
                    FUtil.bcastMsg(player.getName() + " has been flagged for possible freecam building.", ChatColor.RED);
                    FUtil.autoEject(player, "Freecam (extended range) block building is not permitted on this server.");

                    playerdata.resetFreecamPlaceCount();

                    event.setCancelled(true);
                    return;
                }
            }

            Long lastRan = plugin.hb.getLastRan();
            if (lastRan == null || lastRan + TotalFreedomMod.HEARTBEAT_RATE * 1000L < System.currentTimeMillis())
            {
                //TFM_Log.warning("Heartbeat service timeout - can't check block place/break rates.");
            }
            else
            {
                if (playerdata.incrementAndGetBlockPlaceCount() > ConfigEntry.NUKE_MONITOR_COUNT_PLACE.getInteger())
                {
                    FUtil.bcastMsg(player.getName() + " is placing blocks too fast!", ChatColor.RED);
                    FUtil.autoEject(player, "You are placing blocks too fast.");

                    playerdata.resetBlockPlaceCount();

                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (ConfigEntry.PROTECTAREA_ENABLED.getBoolean())
        {
            if (!plugin.al.isAdmin(player))
            {
                if (plugin.pa.isInProtectedArea(blockLocation))
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        switch (event.getBlockPlaced().getType())
        {
            case LAVA:
            case STATIONARY_LAVA:
            {
                if (ConfigEntry.ALLOW_LAVA_PLACE.getBoolean())
                {
                    FLog.info(String.format("%s placed lava @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));

                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                    player.sendMessage(ChatColor.GRAY + "Lava placement is currently disabled.");

                    event.setCancelled(true);
                }
                break;
            }
            case WATER:
            case STATIONARY_WATER:
            {
                if (ConfigEntry.ALLOW_WATER_PLACE.getBoolean())
                {
                    FLog.info(String.format("%s placed water @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));

                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                    player.sendMessage(ChatColor.GRAY + "Water placement is currently disabled.");

                    event.setCancelled(true);
                }
                break;
            }
            case FIRE:
            {
                if (ConfigEntry.ALLOW_FIRE_PLACE.getBoolean())
                {
                    FLog.info(String.format("%s placed fire @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));

                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                    player.sendMessage(ChatColor.GRAY + "Fire placement is currently disabled.");

                    event.setCancelled(true);
                }
                break;
            }
            case TNT:
            {
                if (ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
                {
                    FLog.info(String.format("%s placed TNT @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));

                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));

                    player.sendMessage(ChatColor.GRAY + "TNT is currently disabled.");
                    event.setCancelled(true);
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFromTo(BlockFromToEvent event)
    {
        if (!ConfigEntry.ALLOW_FLUID_SPREAD.getBoolean())
        {
            event.setCancelled(true);
        }
    }
}
