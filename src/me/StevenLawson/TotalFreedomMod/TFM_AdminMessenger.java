package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_AdminMessenger
{
    public static void adminMessengerMsg(CommandSender sender, String message, boolean senderIsConsole)
    {
        String name = sender.getName() + " " + TFM_PlayerRank.fromSender(sender).getPrefix() + ChatColor.WHITE;
        TFM_Log.info("[Admin Support] " + name + ": " + message);

        for (Player player : Bukkit.getOnlinePlayers())
        {
            final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
            player.sendMessage("[" + ChatColor.AQUA + "Admin Support" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + name + ": " + ChatColor.AQUA + message);
        }
    }
}
