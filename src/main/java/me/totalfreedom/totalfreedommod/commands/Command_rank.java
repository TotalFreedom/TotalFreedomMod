package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows your rank.", usage = "/<command>")
public class Command_rank extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole && args.length < 1)
        {
            for (Player player : server.getOnlinePlayers())
            {
                playerMsg(player.getName() + " is " + plugin.rm.getDisplayRank(player).getColoredLoginMessage());
            }
            return true;
        }

        if (args.length > 1)
        {
            return false;
        }

        if (args.length == 0)
        {
            playerMsg(sender.getName() + " is " + plugin.rm.getDisplayRank(playerSender).getColoredLoginMessage(), ChatColor.AQUA);
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        playerMsg(player.getName() + " is " + plugin.rm.getDisplayRank(player).getColoredLoginMessage(), ChatColor.AQUA);

        return true;
    }
}
