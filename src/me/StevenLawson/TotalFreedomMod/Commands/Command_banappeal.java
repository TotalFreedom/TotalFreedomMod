package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "How to get unbanned if you get banned.", usage = "/<command>")
public class Command_banappeal extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
      playerMsg("How to get unbanned if you get banned (or you want a friend unbanned).", ChatColor.GOLD);
    	playerMsg("First of all, open up your internet browser and go to totalfreedom.boards.net", ChatColor.AQUA);
    	playerMsg("You will be greeted with some screen saying you have to login, sign up for the forums.", ChatColor.AQUA);
    	playerMsg("After you sign up, login to the forums and go to the Ban or Admin Suspension Appeals section.", ChatColor.AQUA);
    	playerMsg("Notice the 'How to file a ban appeal' section. Click on it. Once you are there, copy and paste mark's template.", ChatColor.AQUA);
    	playerMsg("Go back to the section and click on Create Thread in the corner.", ChatColor.AQUA);
    	playerMsg("Put in a title, and for the message, paste mark's example. Edit the example to fill out your app, make sure the info is valid. If you have broke a rule, don't say 'I got banned for no reason!' Admit it and apologize and maybe you will be unbanned.", ChatColor.AQUA);
    	playerMsg("It is recommended you put this in a notepad incase you forget this info once you are banned.", ChatColor.RED);
    	
		return senderIsConsole;
    }

}
