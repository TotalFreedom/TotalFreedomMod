package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.BOTH)
@CommandParameters(description = "After an admin has verified, this let's other admins know they have with enhanced chat", usage = "/<command>")
public class Command_verified extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
 {
        
        for (Player player : server.getOnlinePlayers())   
        {
            
            if (isAdmin(player))
            {
             msg(ChatColor.GRAY + "[" + ChatColor.YELLOW + "Imposter" + ChatColor.GRAY + "] " 
             + ChatColor.WHITE + "< " + ChatColor.RESET + "" + sender.getName() + ChatColor.WHITE + "> "
             + ChatColor.YELLOW + "Has verified and is requesting to be added!");
       }

        return true;
    }
}
