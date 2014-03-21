package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.BukkitTelnet.api.TelnetCommandEvent;
import me.StevenLawson.BukkitTelnet.api.TelnetPreLoginEvent;
import me.StevenLawson.TotalFreedomMod.TFM_CommandBlocker;
import me.StevenLawson.TotalFreedomMod.TFM_Superadmin;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
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

        final TFM_Superadmin admin = TFM_SuperadminList.getAdminEntryByIP(ip, true);

        if (admin == null || !(admin.isTelnetAdmin() || admin.isSeniorAdmin()))
        {
            return;
        }

        event.setBypassPassword(true);
        event.setName(admin.getName());

        final OfflinePlayer player = Bukkit.getOfflinePlayer(admin.getName());
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
