package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FSync;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class Editblocker extends FreedomService
{

    public Editblocker(TotalFreedomMod plugin)
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
    public void BlockPlaceEvent(BlockPlaceEvent event)
    {
        FPlayer fPlayer = plugin.pl.getPlayerSync(event.getPlayer());

        if (!fPlayer.isEditBlock())
        {
            return;
        }

        if (plugin.al.isAdminSync(event.getPlayer()))
        {
            fPlayer.setEditBlocked(false);
            return;
        }

        FSync.playerMsg(event.getPlayer(), ChatColor.RED + "Your block placement have been disabled!");
        event.setCancelled(true);
        event.isCancelled(); // is on tfm already and both works fine
    }

    @EventHandler(priority = EventPriority.LOW)
    public void BlockBreakEvent(BlockBreakEvent event)
    {
        FPlayer fPlayer = plugin.pl.getPlayerSync(event.getPlayer());

        if (!fPlayer.isEditBlock())
        {
            return;
        }

        if (plugin.al.isAdminSync(event.getPlayer()))
        {
            fPlayer.setEditBlocked(false);
            return;
        }

        FSync.playerMsg(event.getPlayer(), ChatColor.RED + "Your block breaking have been disabled!");
        event.setCancelled(true);
        event.isCancelled();
    }

}
