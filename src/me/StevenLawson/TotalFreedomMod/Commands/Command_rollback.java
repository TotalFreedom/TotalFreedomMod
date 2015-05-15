package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_RollbackManager;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Issues a rollback on a player", usage = "/<command> <[partialname] | undo [partialname] purge [partialname] | purgeall>", aliases = "rb")
public class Command_rollback extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0 || args.length > 2)
        {
            return false;
        }

        if (args.length == 1)
        {
            if ("purgeall".equals(args[0]))
            {
                TFM_Util.adminAction(sender.getName(), "Purging all rollback history", false);
                playerMsg("Purged all rollback history for " + TFM_RollbackManager.purgeEntries() + " players.");
            }
            else
            {
                final String playerName = TFM_RollbackManager.findPlayer(args[0]);

                if (playerName == null)
                {
                    playerMsg("That player has no entries stored.");
                    return true;
                }

                if (TFM_RollbackManager.canUndoRollback(playerName))
                {
                    playerMsg("That player has just been rolled back.");
                }

                TFM_Util.adminAction(sender.getName(), "Rolling back player: " + playerName, false);
                playerMsg("Rolled back " + TFM_RollbackManager.rollback(playerName) + " edits for " + playerName + ".");
                playerMsg("If this rollback was a mistake, use /rollback undo " + playerName + " within 40 seconds to reverse the rollback.");
            }
            return true;
        }

        if (args.length == 2)
        {
            if ("purge".equalsIgnoreCase(args[0]))
            {
                final String playerName = TFM_RollbackManager.findPlayer(args[1]);

                if (playerName == null)
                {
                    playerMsg("That player has no entries stored.");
                    return true;
                }

                playerMsg("Purged " + TFM_RollbackManager.purgeEntries(playerName) + " rollback history entries for " + playerName + ".");
                return true;
            }

            if ("undo".equalsIgnoreCase(args[0]))
            {
                final String playerName = TFM_RollbackManager.findPlayer(args[1]);

                if (playerName == null)
                {
                    playerMsg("That player hasn't been rolled back recently.");
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Reverting rollback for player: " + playerName, false);
                playerMsg("Reverted " + TFM_RollbackManager.undoRollback(playerName) + " edits for " + playerName + ".");
                return true;
            }
        }

        return false;
    }
}
