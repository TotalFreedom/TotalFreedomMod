package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class AntiNuke extends FreedomService
{

    public AntiNuke(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (!ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
        {
            return;
        }

        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);

        final Location playerLocation = player.getLocation();

        final double nukeMonitorRange = ConfigEntry.NUKE_MONITOR_RANGE.getDouble();

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
            if (fPlayer.incrementAndGetFreecamDestroyCount() > ConfigEntry.FREECAM_TRIGGER_COUNT.getInteger())
            {
                FUtil.bcastMsg(player.getName() + " has been flagged for possible freecam nuking.", ChatColor.RED);
                plugin.ae.autoEject(player, "Freecam (extended range) block breaking is not permitted on this server.");

                fPlayer.resetFreecamDestroyCount();

                event.setCancelled(true);
                return;
            }
        }

        if (fPlayer.incrementAndGetBlockDestroyCount() > ConfigEntry.NUKE_MONITOR_COUNT_BREAK.getInteger())
        {
            FUtil.bcastMsg(player.getName() + " is breaking blocks too fast!", ChatColor.RED);
            plugin.ae.autoEject(player, "You are breaking blocks too fast. Nukers are not permitted on this server.");

            fPlayer.resetBlockDestroyCount();

            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (!ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
        {
            return;
        }

        Player player = event.getPlayer();
        Location blockLocation = event.getBlock().getLocation();
        FPlayer fPlayer = plugin.pl.getPlayer(player);

        Location playerLocation = player.getLocation();

        double nukeMonitorRange = ConfigEntry.NUKE_MONITOR_RANGE.getDouble();

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
            if (fPlayer.incrementAndGetFreecamPlaceCount() > ConfigEntry.FREECAM_TRIGGER_COUNT.getInteger())
            {
                FUtil.bcastMsg(player.getName() + " has been flagged for possible freecam building.", ChatColor.RED);
                plugin.ae.autoEject(player, "Freecam (extended range) block building is not permitted on this server.");

                fPlayer.resetFreecamPlaceCount();

                event.setCancelled(true);
                return;
            }
        }

        if (fPlayer.incrementAndGetBlockPlaceCount() > ConfigEntry.NUKE_MONITOR_COUNT_PLACE.getInteger())
        {
            FUtil.bcastMsg(player.getName() + " is placing blocks too fast!", ChatColor.RED);
            plugin.ae.autoEject(player, "You are placing blocks too fast.");

            fPlayer.resetBlockPlaceCount();

            event.setCancelled(true);
        }
    }
}
