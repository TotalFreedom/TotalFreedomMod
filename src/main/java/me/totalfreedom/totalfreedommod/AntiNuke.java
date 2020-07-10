package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class AntiNuke extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
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
        final FPlayer fPlayer = plugin.pl.getPlayer(player);

        if (fPlayer.incrementAndGetBlockDestroyCount() > ConfigEntry.NUKE_MONITOR_COUNT_BREAK.getInteger())
        {
            FUtil.bcastMsg(player.getName() + " is breaking blocks too fast!", ChatColor.RED);
            //plugin.ae.autoEject(player, "You are breaking blocks too fast. Nukers are not permitted on this server.");
            player.kickPlayer(ChatColor.RED + "You are breaking blocks too fast. Nukers are not permitted on this server.");

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
        FPlayer fPlayer = plugin.pl.getPlayer(player);

        if (fPlayer.incrementAndGetBlockPlaceCount() > ConfigEntry.NUKE_MONITOR_COUNT_PLACE.getInteger())
        {
            FUtil.bcastMsg(player.getName() + " is placing blocks too fast!", ChatColor.RED);
            //plugin.ae.autoEject(player, "You are placing blocks too fast.");
            player.kickPlayer(ChatColor.RED + "You are placing blocks too fast.");

            fPlayer.resetBlockPlaceCount();

            event.setCancelled(true);
        }
    }
}
