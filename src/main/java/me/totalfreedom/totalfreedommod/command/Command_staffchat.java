package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Talk privately with other staff on the server.", usage = "/<command> [message]", aliases = "o,sc")
public class Command_staffchat extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            if (senderIsConsole)
            {
                msg("You must be in-game to toggle staff chat, it cannot be toggled via CONSOLE or Telnet.");
                return true;
            }

            FPlayer userinfo = plugin.pl.getPlayer(playerSender);
            userinfo.setStaffChat(!userinfo.inStaffChat());
            msg("Toggled your staff chat " + (userinfo.inStaffChat() ? "on" : "off") + ".");
        }
        else
        {
            plugin.cm.staffChat(sender, StringUtils.join(args, " "));
        }
        return true;
    }
}
