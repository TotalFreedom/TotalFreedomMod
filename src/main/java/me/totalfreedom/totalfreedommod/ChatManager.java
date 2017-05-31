package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FSync;
import static me.totalfreedom.totalfreedommod.util.FUtil.playerMsg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatManager extends FreedomService
{

    public ChatManager(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChatFormat(AsyncPlayerChatEvent event)
    {
        try
        {
            handleChatEvent(event);
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    private void handleChatEvent(AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();
        String message = event.getMessage().trim();

        // Strip color from messages
        message = ChatColor.stripColor(message);

        // Truncate messages that are too long - 100 characters is vanilla client max
        if (message.length() > 100)
        {
            message = message.substring(0, 100);
            FSync.playerMsg(player, "Message was shortened because it was too long to send.");
        }

        // Check for caps
        if (message.length() >= 6)
        {
            int caps = 0;
            for (char c : message.toCharArray())
            {
                if (Character.isUpperCase(c))
                {
                    caps++;
                }
            }
            if (((float) caps / (float) message.length()) > 0.65) //Compute a ratio so that longer sentences can have more caps.
            {
                message = message.toLowerCase();
            }
        }

        // Check for adminchat
        final FPlayer fPlayer = plugin.pl.getPlayerSync(player);
        if (fPlayer.inAdminChat())
        {
            FSync.adminChatMessage(player, message);
            event.setCancelled(true);
            return;
        }

        // Finally, set message
        event.setMessage(message);

        // Make format
        String format = "<%1$s> %2$s";

        String tag = fPlayer.getTag();
        if (tag != null && !tag.isEmpty())
        {
            format = tag.replace("%", "%%") + " " + format;
        }

        // Set format
        event.setFormat(format);
    }

    public void adminChat(CommandSender sender, String message)
    {
        String name = sender.getName() + " " + plugin.rm.getDisplay(sender).getColoredTag() + ChatColor.WHITE;
        FLog.info("[ADMIN] " + name + ": " + message);

        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player))
            {
                player.sendMessage("[" + ChatColor.AQUA + "ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + name + ": " + ChatColor.GOLD + message);
            }
        }
    }

    public void reportAction(Player reporter, Player reported, String report)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player))
            {
                playerMsg(player, ChatColor.RED + "[REPORTS] " + ChatColor.GOLD + reporter.getName() + " has reported " + reported.getName() + " for " + report);
            }
        }
    }

}
