package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH, blockHostConsole = true, aliases = "cc")
@CommandParameters(description = "Clear the public chat.", usage = "/<command>")
public class Command_clearchat extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Player p = (Player) sender;
        
        // Checks and sends ONLY a blank message to all players on the server (to prevent console spam)
        for (Player player : server.getOnlinePlayers())
        {
            // Run code inside the loop 100 times
            for (int i = 0; i < 100; i++)
            {
                player.sendMessage("");
            }
        }
        
        Bukkit.broadcastMessage(ChatColor.BLUE + "Chat cleared by " + p.getName() + ".");
        return true;
    }
}
