package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Toggle whether or not a player has their inventory automatically cleared when they join", usage = "/<command> <player>")
public class Command_autoclear extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        boolean enabled = plugin.lp.CLEAR_ON_JOIN.contains(args[0]);

        if (enabled)
        {
            plugin.lp.CLEAR_ON_JOIN.remove(args[0]);
        }
        else
        {
            plugin.lp.CLEAR_ON_JOIN.add(args[0]);
        }

        msg(args[0] + " will " + (enabled ? "no longer" : "now") + " have their inventory cleared when they join.");

        return true;
    }
}
