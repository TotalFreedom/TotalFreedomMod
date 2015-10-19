package me.totalfreedom.totalfreedommod.listener;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.pravian.aero.component.PluginListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener extends PluginListener<TotalFreedomMod>
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerPing(ServerListPingEvent event)
    {
        final String ip = event.getAddress().getHostAddress().trim();

        if (plugin.bm.isIpBanned(ip))
        {
            event.setMotd(ChatColor.RED + "You are banned.");
            return;
        }

        if (ConfigEntry.ADMIN_ONLY_MODE.getBoolean())
        {
            event.setMotd(ChatColor.RED + "Server is closed.");
            return;
        }

        if (Bukkit.hasWhitelist())
        {
            event.setMotd(ChatColor.RED + "Whitelist enabled.");
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers())
        {
            event.setMotd(ChatColor.RED + "Server is full.");
            return;
        }

        if (!ConfigEntry.SERVER_COLORFUL_MOTD.getBoolean())
        {
            event.setMotd(FUtil.colorize(ConfigEntry.SERVER_MOTD.getString()
                    .replace("%mcversion%", plugin.si.getVersion())));
            return;
        }
        // Colorful MOTD

        final StringBuilder motd = new StringBuilder();

        for (String word : ConfigEntry.SERVER_MOTD.getString().replace("%mcversion%", plugin.si.getVersion()).split(" "))
        {
            motd.append(FUtil.randomChatColor()).append(word).append(" ");
        }

        event.setMotd(FUtil.colorize(motd.toString()));
    }
}
