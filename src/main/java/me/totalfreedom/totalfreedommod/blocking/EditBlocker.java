package me.totalfreedom.totalfreedommod.blocking;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FSync;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class EditBlocker extends FreedomService
{

    public EditBlocker(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        FPlayer fPlayer = plugin.pl.getPlayerSync(event.getPlayer());
        if (!fPlayer.isEditBlocked())
        {
            return;
        }

        if (plugin.al.isAdminSync(event.getPlayer()))
        {
            fPlayer.setEditBlocked(false);
            return;
        }

        FSync.playerMsg(event.getPlayer(), ChatColor.RED + "Your ability to place blocks has been disabled!");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event)
    {
        FPlayer fPlayer = plugin.pl.getPlayerSync(event.getPlayer());
        if (!fPlayer.isEditBlocked())
        {
            return;
        }

        if (plugin.al.isAdminSync(event.getPlayer()))
        {
            fPlayer.setEditBlocked(false);
            return;
        }

        FSync.playerMsg(event.getPlayer(), ChatColor.RED + "Your ability to destroy blocks has been disabled!");
        event.setCancelled(true);
    }

}
