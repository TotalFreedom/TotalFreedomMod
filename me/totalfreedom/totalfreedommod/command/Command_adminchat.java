package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import me.totalfreedom.totalfreedommod.*;
import org.apache.commons.lang3.*;
import me.totalfreedom.totalfreedommod.player.*;
import me.totalfreedom.totalfreedommod.util.FUtil;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(
        description = "AdminChat - Talk privately with other admins. Using <command> itself will toggle AdminChat on and off for all messages.",
        usage = "/<command> [message...]",
        aliases = "o,ac")
public class Command_adminchat extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            if (senderIsConsole)
            {
                msg("Only in-game players can toggle AdminChat.");
                return true;
            }

            FPlayer userinfo = plugin.pl.getPlayer(playerSender);
            userinfo.setAdminChat(!userinfo.inAdminChat());
            msg("Toggled Admin Chat " + (userinfo.inAdminChat() ? "on" : "off") + ".");
        }
        else
        {
            if(StringUtils.join(args, " ").contains("&k") || StringUtils.join(args, " ").contains("&0") || StringUtils.join(args, " ").contains("&m"))
            {
                msg("You can't use Forbidden Colors!");
                return true; 
            }
            plugin.cm.adminChat(sender, FUtil.colorize(StringUtils.join(args, " ")));
        }
        return true;
    }
}