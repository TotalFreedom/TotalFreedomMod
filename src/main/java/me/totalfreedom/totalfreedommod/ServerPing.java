package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPing extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerPing(ServerListPingEvent event)
    {
        final String ip = event.getAddress().getHostAddress().trim();

        if (plugin.bm.isIpBanned(ip))
        {
            event.setMotd(FUtil.colorize(ConfigEntry.SERVER_BAN_MOTD.getString()));
            return;
        }

            if (ConfigEntry.STAFF_ONLY_MODE.getBoolean())
        {
            event.setMotd(FUtil.colorize(ConfigEntry.SERVER_STAFFMODE_MOTD.getString()));
            return;
        }

        if (LoginProcess.isLockdownEnabled())
        {
            event.setMotd(FUtil.colorize(ConfigEntry.SERVER_LOCKDOWN_MOTD.getString()));
            return;
        }

        if (Bukkit.hasWhitelist())
        {
            event.setMotd(FUtil.colorize(ConfigEntry.SERVER_WHITELIST_MOTD.getString()));
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers())
        {
            event.setMotd(FUtil.colorize(ConfigEntry.SERVER_FULL_MOTD.getString()));
            return;
        }

        String baseMotd = ConfigEntry.SERVER_MOTD.getString().replace("%mcversion%", plugin.si.getVersion());
        baseMotd = baseMotd.replace("\\n", "\n");
        baseMotd = FUtil.colorize(baseMotd);

        if (!ConfigEntry.SERVER_COLORFUL_MOTD.getBoolean())
        {
            event.setMotd(baseMotd);
            return;
        }

        // Colorful MOTD
        final StringBuilder motd = new StringBuilder();
        for (String word : baseMotd.split(" "))
        {
            motd.append(FUtil.randomChatColor()).append(word).append(" ");
        }

        event.setMotd(motd.toString().trim());
    }

}
