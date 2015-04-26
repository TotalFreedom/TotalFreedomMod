package me.StevenLawson.TotalFreedomMod.Bridge;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_ProtectedArea;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.worldedit.LimitChangedEvent;
import me.StevenLawson.worldedit.SelectionChangedEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TFM_WorldEditListener implements Listener
{

    @EventHandler
    public void onSelectionChange(final SelectionChangedEvent event)
    {
        final Player player = event.getPlayer();

        if (TFM_AdminList.isSuperAdmin(player))
        {
            return;
        }

        if (TFM_ProtectedArea.isInProtectedArea(
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

        if (TFM_AdminList.isSuperAdmin(player))
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
            TFM_Util.bcastMsg(event.getPlayer().getName() + " tried to set their WorldEdit limit to " + event.getLimit() + " and has been de-opped", ChatColor.RED);
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot set your limit higher than 10000 or to -1!");
        }
    }

}
