package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Learn how to save your builds with worldedit!", usage = "/<command>", aliases = "sb,")
public class Command_savebuild extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
		if (args.length < 1) {
		
		return false;
		
		}
		
		msg(ChatColor.RED + "How to save your build with WorldEdit on TotalFreedom!");
		msg(ChatColor.GREEN + "First do //wand to get the wand tool to select your build.");
		msg(ChatColor.LIGHT_PURPLE + "Next, you want to select your build in all by Left Clicking one side, and right clicking another.");
		msg(ChatColor.RED + "If that doesn't work, you can use //pos1 and //pos2 to select the build.");
		msg(ChatColor.GREEN + "Once you've selected your build, you should do //copy while standing on the ground level to your build.");
		msg(ChatColor.LIGHT_PURPLE + "After doing that, do //schem save <your schematic name>. " + ChatColor.RED + "NOTE: Do not keep the <> in your schematic save, and be sure to not put spaces in the name either.");
		msg(ChatColor.RED + "Once you've done that, you're finished! " + ChatColor.GREEN + "To load your build do //schem load <schematic name> and you can do //paste to paste your build!");
	
        return true;
    }
}
