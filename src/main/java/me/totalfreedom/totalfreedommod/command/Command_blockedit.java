package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import me.totalfreedom.totalfreedommod.rank.Rank;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Blocks all block placing for player with brute force.", usage = "/<command> [[-s] <player> [reason] | list | purge | all]", aliases = "editmute")
public class Command_blockedit extends FreedomCommand
{

    @Override
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, String[] args, final boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("list"))
        {
            msg("Block edits blocked for players:");
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                final FPlayer info = plugin.pl.getPlayer(mp);
                if (info.isEditBlocked())
                {
                    msg("- " + mp.getName());
                    ++count;
                }
            }

            if (count == 0)
            {
                msg("- none");
            }
            return true;
        }

        if (args[0].equals("purge"))
        {
            FUtil.adminAction(sender.getName(), "Unblocking block edits for all players.", true);
            int count = 0;
            for (final Player mp : this.server.getOnlinePlayers())
            {
                final FPlayer info = plugin.pl.getPlayer(mp);
                if (info.isEditBlocked())
                {
                    info.setEditBlocked(false);
                    ++count;
                }
            }
            msg("Unblocked all block edit for " + count + " players.");
            return true;
        }

        if (args[0].equals("all"))
        {
            FUtil.adminAction(sender.getName(), "Blocking block edits for all non-admins.", true);
            int counter = 0;
            for (final Player player : this.server.getOnlinePlayers())
            {
                if (!plugin.al.isAdmin((CommandSender) player))
                {
                    final FPlayer playerdata = plugin.pl.getPlayer(player);
                    playerdata.setEditBlocked(true);
                    ++counter;
                }
            }

            msg("Blocked block edits for " + counter + " players.");
            return true;
        }

        final boolean smite = args[0].equals("-s");
        if (smite)
        {
            args = (String[]) ArrayUtils.subarray((Object[]) args, 1, args.length);
            if (args.length < 1)
            {
                return false;
            }
        }

        final Player player2 = getPlayer(args[0]);
        if (player2 == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        String reason = null;
        if (args.length > 1)
        {
            reason = StringUtils.join((Object[]) args, " ", 1, args.length);
        }

        final FPlayer playerdata2 = plugin.pl.getPlayer(player2);
        if (playerdata2.isEditBlocked())
        {
            FUtil.adminAction(sender.getName(), "Unblocking block edits for " + player2.getName(), true);
            playerdata2.setEditBlocked(false);
            msg("Unblocking block edits for " + player2.getName());
            msg(player2, "Your block edits have been unblocked.", ChatColor.RED);
        }
        else
        {
            if (plugin.al.isAdmin((CommandSender) player2))
            {
                msg(player2.getName() + " is an admin, and cannot have their block edits blocked.");
                return true;
            }

            FUtil.adminAction(sender.getName(), "Blocking block edits for " + player2.getName(), true);
            playerdata2.setEditBlocked(true);

            if (smite)
            {
                Command_smite.smite(sender, player2, reason);
            }

            msg(player2, "Your block edits have been blocked.", ChatColor.RED);
            msg("Blocked all block edits for " + player2.getName());
        }
        return true;
    }
}
