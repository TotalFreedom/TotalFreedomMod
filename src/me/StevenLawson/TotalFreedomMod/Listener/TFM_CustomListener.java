package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.TotalFreedomMod.TFM_Superadmin;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TFM_CustomListener implements Listener
{
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTelnetPreLogin(me.StevenLawson.BukkitTelnet.TelnetPreLoginEvent event)
    {
        String ip = event.getIp();
        if (ip != null && !ip.isEmpty())
        {
            TFM_Superadmin admin = TFM_SuperadminList.getAdminEntryByIP(ip, true);
            if (admin != null && (admin.isTelnetAdmin() || admin.isSeniorAdmin()))
            {
                event.setBypassPassword(true);
                event.setName(admin.getName());
            }
        }
    }
}
