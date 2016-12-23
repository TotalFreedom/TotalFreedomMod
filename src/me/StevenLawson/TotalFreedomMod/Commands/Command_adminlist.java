package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows all admins", usage = "/<command>", aliases = "al")
public class Command_adminlist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        sender.sendMessage(ChatColor.AQUA + "Super Admins");
        sender.sendMessage(ChatColor.AQUA + StringUtils.join(TFM_AdminList.getSuperAdminNames(), ", "));
        sender.sendMessage(ChatColor.LIGHTGREEN + "Telnet Admins");
        sender.sendMessage(ChatColor.LIGHTGREEN + StringUtils.join(TFM_AdminList.getTelnetAdminNames(), ", "));
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "- Senior Admins -");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + StringUtils.join(TFM_AdminList.getSeniorAdminNames(), ", "));
        sender.sendMessage(ChatColor.RED + "Administration");
        sender.sendMessage(ChatColor.RED + "markbyron, Cowgomooo12, OliverDatGuy, WickedGamingUK, lolz3848, Finest95, MadGeek1450");
        sender.sendMessage(ChatColor.DARK_PURPLE + "Lead Developer");
        sender.sendMessage(ChatColor.DARK_PURPLE + "Prozza, MadGeek1450");
        sender.sendMessage(ChatColor.DARK_PURPLE + "Developers");
        sender.sendMessage(ChatColor.DARK_PURPLE + "Prozza, MadGeek1450, WickedGamingUK, wild1145, Darth");
        sender.sendMessage(ChatColor.BLUE + "Founder - markbyron");
        sender.sendMessage(ChatColor.BLUE + "Co-Owner - MadGeek");
        sender.sendMessage(ChatColor.YELLOW + "/adminlist was created by Savnith!");
    }
}
