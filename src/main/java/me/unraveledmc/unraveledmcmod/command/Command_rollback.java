package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Issues a rollback on a player", usage = "/<command> <name> | undo <name>", aliases = "rb")
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
            final String playerName = args[0];
            FUtil.adminAction(sender.getName(), "Rolling back player: " + playerName, false);
            server.dispatchCommand(sender, "co rb u:" + playerName + " t:6w r:#global");
            msg("If this rollback was a mistake, use /rollback undo " + playerName + " to reverse the rollback.");
            return true;
        }

        if (args.length == 2)
        {
            if ("undo".equalsIgnoreCase(args[0]))
            {
                final String playerName = args[1];
                FUtil.adminAction(sender.getName(), "Reverting rollback for player: " + playerName, false);
                server.dispatchCommand(sender, "co restore u:" + playerName + " t:6w r:#global");
                return true;
            }
        }
        return false;
    }
}
