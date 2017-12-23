package me.totalfreedom.totalfreedommod;

import org.bukkit.event.EventPriority;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import java.text.DecimalFormat;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.entity.Player;

public class DropMonitor extends FreedomService
{

    DecimalFormat df;

    public DropMonitor(TotalFreedomMod plugin)
    {
        super(plugin);
        this.df = new DecimalFormat("#");
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    public String GetMaterial(final int id)
    {
        return String.valueOf(Material.getMaterial(id));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemDrop(final PlayerDropItemEvent event)
    {
        if (plugin.al.isAdmin(event.getPlayer()))
        {
            return;
        }
        final int dropeditem = event.getItemDrop().getItemStack().getTypeId();
        final Location loc = event.getPlayer().getLocation();
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).isDropMonitorEnabled())
            {
                FUtil.playerMsg(player, event.getPlayer().getName() + " dropped " + event.getItemDrop().getItemStack().getAmount() + " " + this.GetMaterial(dropeditem) + " at [" + this.df.format(loc.getX()) + ", " + this.df.format(loc.getY()) + ", " + this.df.format(loc.getZ()) + "] at the world '" + loc.getWorld().getName() + "'.");
            }
        }
    }
}
