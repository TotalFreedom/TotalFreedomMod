package me.StevenLawson.TotalFreedomMod.Commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Displays how to compile TotalFreedomMod!.", usage = "/<command>", aliases = "ctfm")
public class Command_compiletfm extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        
        sender.sendMessage(ChatColor.GOLD + "Well, you want to learn how to compile our TotalFreedomMod v4.3?");
        sender.sendMessage(ChatColor.GOLD + "Well, then I will tell you how to and what jars you need!");
        sender.sendMessage(ChatColor.RED + "TotalFreedomMod: github.com/TotalFreedom/TotalFreedomMod");
        sender.sendMessage(ChatColor.RED + "BukkitTelnet: github.com/TotalFreedom/BukkitTelnet");
        sender.sendMessage(ChatColor.RED + "Spigot: Spigot 1.8.8, sorry no link, you will have to contain one from either a website or compile BuildTools");
        sender.sendMessage(ChatColor.RED + "Spigot Essentials: https://hub.spigotmc.org/jenkins/job/Spigot-Essentials/");
        sender.sendMessage(ChatColor.RED + "TF-WorldEdit: github.com/TotalFreedom/TF-WorldEdit/releases");
        sender.sendMessage(ChatColor.AQUA + "NOTE: Compile BukkitTelnet with spigot 1.8.8 ONLY, then add it to the TotalFreedomMod");
        sender.sendMessage(ChatColor.AQUA + "At the end, all the jar files you will have are spigot 1.8.8, BukkitTelnet, TF-WorldEdit, and Spigot Essentials");
        
        return true;
    }
    
}
