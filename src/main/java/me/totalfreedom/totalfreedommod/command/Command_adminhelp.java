package me.totalfreedom.totalfreedommod.command;


import me.totalfreedom.totalfreedommod.command.CommandParameters;
import me.totalfreedom.totalfreedommod.command.CommandPermissions;
import me.totalfreedom.totalfreedommod.command.FreedomCommand;
import me.totalfreedom.totalfreedommod.command.SourceType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME, blockHostConsole = true)
@CommandParameters(description = "Need help from a admin? Use this!", usage = "/<command> <message to admins... >", aliases = "ah,helpme,contactadmins")
public class Command_adminhelp extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
    
            if (args.length < 1) {
    return false;
}
    Player player = (Player) sender;
        
        
        String message = StringUtils.join(ArrayUtils.subarray(args, 0, args.length), " ");
 
    plugin.cm.adminHelp(playerSender, message);
    msg(ChatColor.GOLD + "Thank you! All online admins have been notified.");

    return true;
    }
}
