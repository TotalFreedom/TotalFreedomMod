package me.totalfreedom.totalfreedommod;

import com.google.common.base.Strings;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FSync;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.admin.Admin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import static me.totalfreedom.totalfreedommod.util.FUtil.playerMsg;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

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
        
        // Format colors and strip &k
        message = FUtil.colorize(message);
        message = message.replaceAll(ChatColor.MAGIC.toString(), "&k");
        
        // Truncate messages that are too long - 256 characters is vanilla client max
        if (message.length() > 256)
        {
            message = message.substring(0, 256);
            FSync.playerMsg(player, "Message was shortened because it was too long to send.");
        }

        // Check for adminchat
        final FPlayer fPlayer = plugin.pl.getPlayerSync(player);
        if (fPlayer.inAdminChat())
        {
            FSync.adminChatMessage(player, message);
            event.setCancelled(true);
            return;
        }

        // Check for 4chan trigger
        Boolean green = ChatColor.stripColor(message).toLowerCase().startsWith(">");
        Boolean orange = ChatColor.stripColor(message).toLowerCase().endsWith("<");
        if (ConfigEntry.FOURCHAN_ENABLED.getBoolean())
        {
            if (green)
            {
                message = ChatColor.GREEN + message;
            }
            else if (orange)
            {
                message = ChatColor.GOLD + message;
            }
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
        
        // Check for mentions
        Boolean mentionEveryone = ChatColor.stripColor(message).toLowerCase().contains("@everyone") && plugin.al.isAdmin(player);
        for (Player p : server.getOnlinePlayers())
        {
            if (ChatColor.stripColor(message).toLowerCase().contains("@" + p.getName().toLowerCase()) || mentionEveryone)
            {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1337F, 0.9F);
            }
        }

        // Set format
        event.setFormat(format);

        // Send to discord
        plugin.dc.messageChatChannel(player.getName() + " \u00BB " + ChatColor.stripColor(message));
    }

    public ChatColor getColor(Admin admin, Displayable display)
    {
        ChatColor color = display.getColor();
        if (admin.getOldTags())
        {

            if (color.equals(ChatColor.AQUA))
            {
                color = ChatColor.GOLD;
            }
            else if (color.equals(ChatColor.GOLD))
            {
                color = ChatColor.LIGHT_PURPLE;
            }
            else if (color.equals(ChatColor.DARK_RED))
            {
                color = ChatColor.BLUE;
            }
        }
        return color;
    }

    public String getColoredTag(Admin admin, Displayable display)
    {
        ChatColor color = display.getColor();
        if (admin.getOldTags())
        {

            if (color.equals(ChatColor.AQUA))
            {
                color = ChatColor.GOLD;
            }
            else if (color.equals(ChatColor.GOLD))
            {
                color = ChatColor.LIGHT_PURPLE;
            }
            else if (color.equals(ChatColor.DARK_RED))
            {
                color = ChatColor.BLUE;
            }
        }
        return color + display.getAbbr();
    }

    public void adminChat(CommandSender sender, String message)
    {
        Displayable display = plugin.rm.getDisplay(sender);
        FLog.info("[ADMIN] " + sender.getName() + " " + display.getTag() + ": " + message, true);

        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player))
            {
                Admin admin = plugin.al.getAdmin(player);
                if (!Strings.isNullOrEmpty(admin.getAcFormat()))
                {
                    String format = admin.getAcFormat();
                    ChatColor color = getColor(admin, display);
                    String msg = format.replace("%name%", sender.getName()).replace("%rank%", display.getAbbr()).replace("%rankcolor%", color.toString()).replace("%msg%", message);
                    player.sendMessage(FUtil.colorize(msg));
                }
                else
                {
                    player.sendMessage("[" + ChatColor.AQUA + "ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + sender.getName() + ChatColor.DARK_GRAY + " [" + getColoredTag(admin, display) + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + ": " + ChatColor.GOLD + FUtil.colorize(message));
                }
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
