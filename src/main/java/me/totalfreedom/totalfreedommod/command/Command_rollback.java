package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Issues a rollback on a player", usage = "/<command> <[partialname] | undo [partialname] purge [partialname] | purgeall>", aliases = "rb")
public class Command_rollback extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0 || args.length > 2)
        {
            return false;
        }

        if (args.length == 1)
        {
            if ("purgeall".equals(args[0]))
            {
                FUtil.adminAction(sender.getName(), "Purging all rollback history", false);
                msg("Purged all rollback history for " + plugin.rb.purgeEntries() + " players.");
            }
            else
            {
                final String playerName = plugin.rb.findPlayer(args[0]);

                if (playerName == null)
                {
                    msg("That player has no entries stored.");
                    return true;
                }

                if (plugin.rb.canUndoRollback(playerName))
                {
                    msg("That player has just been rolled back.");
                }

                FUtil.adminAction(sender.getName(), "Rolling back player: " + playerName, false);
                msg("Rolled back " + plugin.rb.rollback(playerName) + " edits for " + playerName + ".");
                msg("If this rollback was a mistake, use /rollback undo " + playerName + " within 40 seconds to reverse the rollback.");
            }
            return true;
        }

        if (args.length == 2)
        {
            if ("purge".equalsIgnoreCase(args[0]))
            {
                final String playerName = plugin.rb.findPlayer(args[1]);

                if (playerName == null)
                {
                    msg("That player has no entries stored.");
                    return true;
                }

                msg("Purged " + plugin.rb.purgeEntries(playerName) + " rollback history entries for " + playerName + ".");
                return true;
            }

            if ("undo".equalsIgnoreCase(args[0]))
            {
                final String playerName = plugin.rb.findPlayer(args[1]);

                if (playerName == null)
                {
                    msg("That player hasn't been rolled back recently.");
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Reverting rollback for player: " + playerName, false);
                msg("Reverted " + plugin.rb.undoRollback(playerName) + " edits for " + playerName + ".");
                return true;
            }
        }

        return false;
    }
}
