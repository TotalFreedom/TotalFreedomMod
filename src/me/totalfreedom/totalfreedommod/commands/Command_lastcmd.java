package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Show the last command that someone used.", usage = "/<command> <player>")
public class Command_lastcmd extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            playerMsg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        final FPlayer playerdata = plugin.pl.getPlayer(player);

        if (playerdata != null)
        {
            String lastCommand = playerdata.getLastCommand();
            if (lastCommand.isEmpty())
            {
                lastCommand = "(none)";
            }
            playerMsg(player.getName() + " - Last Command: " + lastCommand, ChatColor.GRAY);
        }

        return true;
    }
}
