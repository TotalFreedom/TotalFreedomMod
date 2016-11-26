package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Shows information about admin ranks on Total Freedom.", usage = "/<command> <superadmin|telnetadmin|senioradmin>")
public class Command_adminfeatures extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            sender.sendMessage(ChatColor.RED + "This command is to find out what you can do as admin.");
            sender.sendMessage(ChatColor.RED + "You need to use /adminfeatures <superadmin|telnetadmin|senioradmin>.");
        }
        else if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("superadmin"))
            {
                sender.sendMessage(ChatColor.AQUA + "Superadmin is the very first rank you get on Total Freedom. To learn how to get it, use /ai.");
                sender.sendMessage(ChatColor.AQUA + "As superadmin, you can punish bad players, use the adminchat, use other fun commands, bypass bans, and even kick someone when the server is full and you can't join!");
                sender.sendMessage(ChatColor.AQUA + "There is a lot more you can do as admin like remove everyones nickname and see everyones commands. Being a superadmin is very fun on Total Freedom!");
                sender.sendMessage(ChatColor.AQUA + "Hopefully this helps you learn what superadmins can do.");
            }
            else if (args[0].equalsIgnoreCase("telnetadmin"))
            {
                sender.sendMessage(ChatColor.AQUA + "Telnet Admin is the second rank you get on Total Freedom.");
                sender.sendMessage(ChatColor.AQUA + "As a Telnet Admin, you get access to a server CONSOLE!");
                sender.sendMessage(ChatColor.AQUA + "So you can monitor the server without even having to have Java, or on the go.");
                sender.sendMessage(ChatColor.AQUA + "You also get more powers as Telnet Admin, like changing whether the server is cracked or not!");
                sender.sendMessage(ChatColor.AQUA + "Being a Telnet Admin is very fun, and hopefully this helps you learn about Telnet Admins!");
            }
            else if (args[0].equalsIgnoreCase("senioradmin"))
            {
                sender.sendMessage(ChatColor.AQUA + "Senior Admin is the very last rank you get on Total Freedom, however there are certain jobs and stuff that you can get as Senior Admin.");
                sender.sendMessage(ChatColor.AQUA + "As a Senior Admin, you get access to the server control panel, control of admins, MUCH more powers like controlling the plugins, and SO MUCH MORE!");
                sender.sendMessage(ChatColor.AQUA + "It is very hard to get Senior Admin and it is hard work being one, however it also is fun being a Senior Admin!");
                sender.sendMessage(ChatColor.AQUA + "Hopefully this helps you learn about Senior Admins!");
            }
        }
        return true;
    }
}
