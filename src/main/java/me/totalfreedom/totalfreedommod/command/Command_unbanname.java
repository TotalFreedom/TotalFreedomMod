package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Unbans the specified name.", usage = "/<command> <name> [-q]")
public class Command_unbanname extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        boolean silent = false;

        String name = args[0];

        Ban ban = plugin.bm.getByUsername(name);

        if (ban == null)
        {
            msg("The name " + name + " is not banned", ChatColor.RED);
            return true;
        }

        if (ban.hasIps())
        {
            msg("This ban is not a name-only ban.");
            return true;
        }

        if (args.length > 1 && args[1].equals("-q"))
        {
            silent = true;
        }

        plugin.bm.removeBan(ban);

        if (!silent)
        {
            FUtil.staffAction(sender.getName(), "Unbanned the name " + name, true);
        }

        return true;
    }
}