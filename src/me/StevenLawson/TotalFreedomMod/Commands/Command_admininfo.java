package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Apply for admin.", usage = "/<command>", aliases = "ai")
public class Command_admininfo extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            sender.sendMessage(ChatColor.GOLD + "============HOW TO APPLY============");
            sender.sendMessage(ChatColor.AQUA + "If you want to be an administrator, you need to follow these steps:");
            sender.sendMessage(ChatColor.AQUA + "First of all you need to register a forum account at http://immafreedom.eu/");
            sender.sendMessage(ChatColor.AQUA + "Secondly, copy the template at http://immafreedom.eu/showthread.php?tid=3");
            sender.sendMessage(ChatColor.AQUA + "After that, make a new thread by going to this link http://immafreedom.eu/newthread.php?fid=9");
            sender.sendMessage(ChatColor.AQUA + "Finally, paste the template, and answer all of the questions.");
            sender.sendMessage(ChatColor.RED + "Please make sure you meet all the requirements before you submit your application!");
            sender.sendMessage(ChatColor.GOLD + "============HOW TO APPLY============");
            return true;
        }
        else
        {
            return false;
        }
    }
}
