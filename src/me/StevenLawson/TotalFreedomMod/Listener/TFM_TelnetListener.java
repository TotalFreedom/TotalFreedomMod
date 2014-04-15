package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.BukkitTelnet.api.TelnetCommandEvent;
import me.StevenLawson.BukkitTelnet.api.TelnetPreLoginEvent;
import me.StevenLawson.TotalFreedomMod.TFM_CommandBlocker;
import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TFM_TelnetListener implements Listener
{
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTelnetPreLogin(TelnetPreLoginEvent event)
    {

        final String ip = event.getIp();
        if (ip == null || ip.isEmpty())
        {
            return;
        }

        final TFM_Admin admin = TFM_AdminList.getEntryByIp(ip, true);

        if (admin == null || !admin.isTelnetAdmin())
        {
            return;
        }

        event.setBypassPassword(true);
        event.setName(admin.getLastLoginName());

        final OfflinePlayer player = Bukkit.getOfflinePlayer(admin.getLastLoginName());
        if (player == null)
        {
            return;
        }

        event.setName(player.getName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTelnetCommand(TelnetCommandEvent event)
    {
        if (TFM_CommandBlocker.getInstance().isCommandBlocked(event.getCommand(), event.getSender()))
        {
            event.setCancelled(true);
        }
    }
}
