package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Manually verifies a player", usage = "/<command> <partialname>")
public class Command_verifyplayer extends FreedomCommand
{

    @Override
    public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }
        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
            return true;
        }
        if (!plugin.pv.isPlayerImpostor(player))
        {
            msg("That player is not an impostor.");
            return true;
        }
        FUtil.adminAction(sender.getName(), "Manually verifying player " + player.getName(), true);
        plugin.pv.verifyPlayer(player);
        plugin.rm.updateDisplay(player);

        return true;
    }
}