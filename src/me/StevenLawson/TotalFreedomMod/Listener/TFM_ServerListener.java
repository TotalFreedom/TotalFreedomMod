package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class TFM_ServerListener implements Listener
{
    // CommandBlockSetEvent does not exist in "vanilla" Bukkit/CraftBukkit.
    // Comment this method out if you want to compile this without a custom CraftBukkit.
    // Just make sure that enable-command-block=false in server.properties.
    // -Madgeek
    /* Temporary: Until we get a custom CB build out
     @EventHandler(priority = EventPriority.NORMAL)
     public void onCommandBlockSet(org.bukkit.event.server.CommandBlockSetEvent event)
     {
     Player player = event.getPlayer();
     String newCommandRaw = event.getNewCommand();

     if (!TFM_SuperadminList.isSeniorAdmin(player, true))
     {
     player.sendMessage(ChatColor.GRAY + "Only senior admins may set command block commands.");
     event.setCancelled(true);
     return;
     }

     Matcher matcher = Pattern.compile("^/?(\\S+)").matcher(newCommandRaw);
     if (matcher.find())
     {
     String topLevelCommand = matcher.group(1);
     if (topLevelCommand != null)
     {
     topLevelCommand = topLevelCommand.toLowerCase().trim();

     // We need to make it look like the command is coming from the console, so keep the player's name without the Player instance via dummy:
     if (TFM_CommandBlocker.getInstance().isCommandBlocked(topLevelCommand, new TFM_ServerListener_DummyCommandSender(player.getName()), false))
     {
     player.sendMessage(ChatColor.GRAY + "That command is blocked.");
     event.setCancelled(true);
     }
     }
     }
     }*/
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerPing(ServerListPingEvent event)
    {
        final String ip = event.getAddress().getHostAddress();

        if (TFM_BanManager.isIpBanned(ip))
        {
            event.setMotd(ChatColor.RED + "You are banned.");
            return;
        }

        if (TFM_ConfigEntry.ADMIN_ONLY_MODE.getBoolean())
        {
            event.setMotd(ChatColor.RED + "Server is closed.");
            return;
        }

        if (Bukkit.hasWhitelist())
        {
            event.setMotd(ChatColor.RED + "Whitelist enabled.");
            return;
        }

        if (Bukkit.getOnlinePlayers().length >= Bukkit.getMaxPlayers())
        {
            event.setMotd(ChatColor.RED + "Server is full.");
            return;
        }

        if (!TFM_ConfigEntry.SERVER_COLORFUL_MOTD.getBoolean())
        {
            event.setMotd(TFM_Util.colorize(TFM_ConfigEntry.SERVER_MOTD.getString()
                    .replace("%mcversion%", TFM_ServerInterface.getVersion())));
            return;
        }
        // Colorful MOTD

        final StringBuilder motd = new StringBuilder();

        for (String word : TFM_ConfigEntry.SERVER_MOTD.getString().replace("%mcversion%", TFM_ServerInterface.getVersion()).split(" "))
        {
            motd.append(TFM_Util.randomChatColor()).append(word).append(" ");
        }

        event.setMotd(TFM_Util.colorize(motd.toString()));
    }
}
