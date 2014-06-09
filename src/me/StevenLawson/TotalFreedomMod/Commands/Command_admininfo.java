package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Shows you how to become a admin.", usage = "/<command>")
public class Command_admininfo extends TFM_Command
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if(args.length != 0)
        {
            return false;
        }
        else
        {
            playerMsg(ChatColor.AQUA + "The following is accurate as of 09/06/2014");
            playerMsg(ChatColor.AQUA + "To apply for admin you need to go to the forums at http://totalfreedom.boards.net/");
            playerMsg(ChatColor.AQUA + "Then read the requirements.");
            playerMsg(ChatColor.AQUA + "Then if you feel you are ready, make a new thread in the appropriate board.");
            playerMsg(ChatColor.AQUA + "And fill out the template in the new thread.");
            playerMsg(ChatColor.RED + "We ask for you not to ask existing admins for recommendations, this will get your application denied.");
            playerMsg(ChatColor.AQUA + "Good Luck!");
            return true;
        }
    }
}
