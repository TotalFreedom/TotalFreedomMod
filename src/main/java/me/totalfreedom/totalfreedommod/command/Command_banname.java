package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Bans the specified name.", usage = "/<command> <name> [reason] [-q]")
public class Command_banname extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        boolean silent = false;

        String reason = null;
        
        String name = args[0];;

        if (plugin.bm.getByUsername(name) != null)
        {
            msg("The name " + name + " is already banned", ChatColor.RED);
            return true;
        }

        if (args[args.length - 1].equalsIgnoreCase("-q"))
        {
            silent = true;

            if (args.length >= 2)
            {
                reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length - 1), " ");
            }
        }
        else if (args.length > 1)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        }

        // Ban player
        Ban ban = Ban.forPlayerName(name, sender, null, reason);
        plugin.bm.addBan(ban);

        if (!silent)
        {
            FUtil.staffAction(sender.getName(), "Banned the name " + name, true);
        }

        Player player = getPlayer(name);
        if (player != null)
        {
            player.kickPlayer(ban.bakeKickMessage());
        }
        return true;
    }
}