package me.totalfreedom.totalfreedommod.bridge;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FUtil;
// These imports are not in the latest releases of WorldEdit, and the new versions of WorldEdit do not build properly. This will need to be reverted once the new WorldEdit builds are building properly.
//
//import me.totalfreedom.worldedit.LimitChangedEvent;
//import me.totalfreedom.worldedit.SelectionChangedEvent;
//
//The following two imports are a temporary measure as mentioned above.
//
import me.StevenLawson.worldedit.LimitChangedEvent;
import me.StevenLawson.worldedit.SelectionChangedEvent;
//
import net.pravian.aero.component.PluginListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class WorldEditListener extends PluginListener<TotalFreedomMod>
{

    public WorldEditListener(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @EventHandler
    public void onSelectionChange(final SelectionChangedEvent event)
    {
        final Player player = event.getPlayer();

        if (plugin.al.isAdmin(player))
        {
            return;
        }

        if (plugin.pa.isInProtectedArea(
                event.getMinVector(),
                event.getMaxVector(),
                event.getWorld().getName()))
        {

            player.sendMessage(ChatColor.RED + "The region that you selected contained a protected area. Selection cleared.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLimitChanged(LimitChangedEvent event)
    {
        final Player player = event.getPlayer();

        if (plugin.al.isAdmin(player))
        {
            return;
        }

        if (!event.getPlayer().equals(event.getTarget()))
        {
            player.sendMessage(ChatColor.RED + "Only admins can change the limit for other players!");
            event.setCancelled(true);
        }

        if (event.getLimit() < 0 || event.getLimit() > 10000)
        {
            player.setOp(false);
            FUtil.bcastMsg(event.getPlayer().getName() + " tried to set their WorldEdit limit to " + event.getLimit() + " and has been de-opped", ChatColor.RED);
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot set your limit higher than 10000 or to -1!");
        }
    }

}
