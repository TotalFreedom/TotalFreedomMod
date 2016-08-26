package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Block all commands for a specific player.", usage = "/<command> <-a | purge | <player>>", aliases = "blockcommands,blockcommand,bc,bcmd")
public class Command_blockcmd extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equals("purge"))
        {
            FUtil.adminAction(sender.getName(), "Unblocking commands for all players", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                FPlayer playerdata = plugin.pl.getPlayer(player);
                if (playerdata.allCommandsBlocked())
                {
                    counter += 1;
                    playerdata.setCommandsBlocked(false);
                }
            }
            msg("Unblocked commands for " + counter + " players.");
            return true;
        }

        if (args[0].equals("-a"))
        {
            FUtil.adminAction(sender.getName(), "Blocking commands for all non-admins", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (isAdmin(player))
                {
                    continue;
                }

                counter += 1;
                plugin.pl.getPlayer(player).setCommandsBlocked(true);
                msg(player, "Your commands have been blocked by an admin.", ChatColor.RED);
            }

            msg("Blocked commands for " + counter + " players.");
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        if (isAdmin(player))
        {
            msg(player.getName() + " is a Superadmin, and cannot have their commands blocked.");
            return true;
        }

        FPlayer playerdata = plugin.pl.getPlayer(player);

        playerdata.setCommandsBlocked(!playerdata.allCommandsBlocked());

        FUtil.adminAction(sender.getName(), (playerdata.allCommandsBlocked() ? "B" : "Unb") + "locking all commands for " + player.getName(), true);
        msg((playerdata.allCommandsBlocked() ? "B" : "Unb") + "locked all commands.");

        return true;
    }
}
