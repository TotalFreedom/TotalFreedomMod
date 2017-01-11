package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "View important links..", usage = "/<command>")
public class Command_links extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            sender.sendMessage(ChatColor.GOLD + "Important Links you will want to go to for information");
            sender.sendMessage(ChatColor.AQUA + "http://totalfreedom.me/");
            sender.sendMessage(ChatColor.GOLD + "http://totalfreedom.boards.net/");
            sender.sendMessage(ChatColor.AQUA + "Code of Conduct: https://pravi.us/banpolicy/");
            sender.sendMesasge(ChatColor.GOLD + "FAQ: https://pravi.us/faq/");
            return true;
        }
        else
        {
            return false;
        }
    }
}
