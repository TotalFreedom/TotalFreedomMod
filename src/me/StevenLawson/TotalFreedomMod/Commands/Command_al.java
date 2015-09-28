package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows all admins", usage = "/<command>", aliases = "adminlist")
public class Command_al extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        sender.sendMessage(ChatColor.AQUA + "- Super Admins -");
        sender.sendMessage(ChatColor.AQUA + StringUtils.join(TFM_AdminList.getSuperAdminNames(), ", "));
        sender.sendMessage(ChatColor.DARK_GREEN + "- Telnet Admins -");
        sender.sendMessage(ChatColor.DARK_GREEN + StringUtils.join(TFM_AdminList.getTelnetAdminNames(), ", "));
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "- Senior Admins -");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + StringUtils.join(TFM_AdminList.getSeniorAdminNames(), ", "));
        sender.sendMessage(ChatColor.DARK_RED + "Executives: KM_Galahad, Exotic_Starlight");
        sender.sendMessage(ChatColor.DARK_RED + "System Admins: None.");
        sender.sendMessage(ChatColor.DARK_PURPLE + "Developers: AwesomePinch");
        sender.sendMessage(ChatColor.BLUE + "Owners: aggelosQQ");

        return true;
    }
