package me.totalfreedom.totalfreedommod;

import org.bukkit.event.player.*;
import org.bukkit.event.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import me.totalfreedom.totalfreedommod.player.*;
import java.util.*;
import me.totalfreedom.totalfreedommod.util.*;

public class ChatManager extends FreedomService
{
    public ChatManager(final TotalFreedomMod plugin) {
        super(plugin);
    }
    
    protected void onStart() {
    }
    
    protected void onStop() {
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChatFormat(final AsyncPlayerChatEvent event) {
        try {
            this.handleChatEvent(event);
        }
        catch (Exception ex) {
            FLog.severe(ex);
        }
    }
    
    private void handleChatEvent(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        String message = event.getMessage().trim();
        message = ChatColor.stripColor(message);
        if (message.length() > 100) {
            message = message.substring(0, 100);
            FSync.playerMsg(player, "Message was shortened because it was too long to send.");
        }
        if (message.length() >= 6) {
            int caps = 0;
            for (final char c : message.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    ++caps;
                }
            }
            if (caps / message.length() > 0.65) {
                message = message.toLowerCase();
            }
        }
        final FPlayer fPlayer = ((TotalFreedomMod)this.plugin).pl.getPlayerSync(player);
        if (fPlayer.inAdminChat()) {
            FSync.adminChatMessage((CommandSender)player, message);
            event.setCancelled(true);
            return;
        }
        event.setMessage(message);
        String format = "<%1$s> %2$s";
        final String tag = fPlayer.getTag();
        if (tag != null && !tag.isEmpty()) {
            format = tag.replace("%", "%%") + " " + format;
        }
        event.setFormat(format);
    }
    
    public void adminChat(final CommandSender sender, final String message) {
        final String name = sender.getName() + " " + ((TotalFreedomMod)this.plugin).rm.getDisplay(sender).getColoredTag() + ChatColor.WHITE;
        FLog.info("[ADMIN] " + name + ": " + message);
        for (final Player player : this.server.getOnlinePlayers()) {
            if (((TotalFreedomMod)this.plugin).al.isAdmin((CommandSender)player)) {
                player.sendMessage("[" + ChatColor.AQUA + "ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + name + ": " + ChatColor.GOLD + FUtil.colorize(message));
            }
        }
    }
    
    public void adminHelp(Player sender, String message) {
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player))
            {
                player.sendMessage(ChatColor.RED + "[ADMINHELP] " + ChatColor.GOLD + sender.getName() + ": " + message);
            }
        }
    }

    public void reportAction(final Player reporter, final Player reported, final String report) {
        for (final Player player : this.server.getOnlinePlayers()) {
            if (((TotalFreedomMod)this.plugin).al.isAdmin((CommandSender)player)) {
                FUtil.playerMsg((CommandSender)player, ChatColor.RED + "[REPORTS] " + ChatColor.GOLD + reporter.getName() + " has reported " + reported.getName() + " for " + report);
            }
        }
    }
}
