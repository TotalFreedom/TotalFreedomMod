package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_status extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        sender.sendMessage(ChatColor.GOLD + "Madgeek1450's Total Freedom Mod v" + TotalFreedomMod.pluginVersion + "." + TotalFreedomMod.buildNumber + ", built " + TotalFreedomMod.buildDate);
        sender.sendMessage(ChatColor.YELLOW + "Server is currently running with 'online-mode=" + (server.getOnlineMode() ? "true" : "false") + "'.");
        sender.sendMessage(ChatColor.GOLD + "Loaded worlds:");

        int i = 0;
        for (World world : server.getWorlds())
        {
            sender.sendMessage(ChatColor.GOLD + "World " + Integer.toString(i++) + ": " + world.getName() + " - " + Integer.toString(world.getPlayers().size()) + " players.");
        }

        return true;
    }
}
