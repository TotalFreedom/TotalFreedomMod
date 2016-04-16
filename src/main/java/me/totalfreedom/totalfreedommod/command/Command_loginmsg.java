package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Set your own login message", usage = "/<command> <set <message>>")
public class Command_loginmsg extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {   
        if (args.length < 2)
        {
            return false;
        }
        
        String message = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        
        String commandSender = sender.getName();
        
        if (!args[0].equalsIgnoreCase(commandSender) && !plugin.al.isSeniorAdmin(sender))
        {
            msg("You don't have permission to assign another users login message!");
            return true;
        }
        
        if (args[1].equalsIgnoreCase("delete"))
        {
            admin.setLoginMssgae(null);
            msg("You have deleted your login message!");
            return true;
        }
        
        if (args[1].equalsIgnoreCase("set"))
        {
            Admin admin = plugin.al.getAdmin(sender);
            
            if (message.equalsIgnoreCase(admin.getLoginMessage()))
            {
                msg(ChatColor.RED + "The login message can't be the same as its the same as the current login message");
                return true;
            }
            
            admin.setLoginMessage(message);
            msg(FUtil.colorize("The newly set login message is now " + message), ChatColor.GREEN);
            return true;
        }
        return false;
    }
}
