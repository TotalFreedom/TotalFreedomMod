package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows ranks", usage = "/<command> [player]")
public class Command_rank extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole && args.length < 1)
        {
            for (Player player : server.getOnlinePlayers())
            {
                msg(player.getName() + " is " + plugin.rm.getDisplay(player).getColoredLoginMessage());
            }
            return true;
        }

        if (args.length > 1)
        {
            return false;
        }

        if (args.length == 0)
        {
            msg(sender.getName() + " is " + plugin.rm.getDisplay(sender).getColoredLoginMessage(), ChatColor.AQUA);
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        msg(player.getName() + " is " + plugin.rm.getDisplay(player).getColoredLoginMessage(), ChatColor.AQUA);

        return true;
    }
}
