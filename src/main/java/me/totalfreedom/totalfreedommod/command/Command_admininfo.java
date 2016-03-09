package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME, blockHostConsole = true)
@CommandParameters(
		description = "Learn how to apply for admin.", 
		usage = "/<command> [on | off]",
		aliases = "ai")
public class Command_admininfo extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
    	sender.sendMessage(ChatColor.RED + "So, you want to apply for admin ay?");
    	sender.sendMessage(ChatColor.RED + "Well head on over to totalfreedom.boards.net and register!");
    	sender.sendMessage(ChatColor.RED + "If you just registered an account, wait one hour to 24 hours for your account to be approved, It's just our protection.");
    	sender.sendMessage(ChatColor.RED + "Go to the Applications Board, go to the template and get ready for the next step;");
    	sender.sendMessage(ChatColor.RED + "Then you just copy down the questions and make a new thread in the Applications board, but make sure that you answer all the questions!");
    	sender.sendMessage(ChatColor.RED + "Also, make sure you meet ALL the requirements, otherwise your application will be Auto-Denied!");
    	sender.sendMessage(ChatColor.AQUA + "Good Luck Applying, " + sender.getName());

    	return true;
}
}
