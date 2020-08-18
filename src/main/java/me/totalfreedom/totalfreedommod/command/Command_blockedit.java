package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Restricts/unrestricts block modification abilities for everyone on the server or a certain player.", usage = "/<command> [[-s] <player> [reason] | list | purge | all]")
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
            msg("The following have block modification abilities restricted:");
            int count = 0;
            for (Player player : server.getOnlinePlayers())
            {
                final FPlayer info = plugin.pl.getPlayer(player);
                if (info.isEditBlocked())
                {
                    msg("- " + player.getName());
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
            FUtil.staffAction(sender.getName(), "Unblocking block modification abilities for all players.", true);
            int count = 0;
            for (final Player player : this.server.getOnlinePlayers())
            {
                final FPlayer info = plugin.pl.getPlayer(player);
                if (info.isEditBlocked())
                {
                    info.setEditBlocked(false);
                    ++count;
                }
            }
            msg("Unblocked all block modification abilities for " + count + " players.");
            return true;
        }

        if (args[0].equals("all"))
        {
            FUtil.staffAction(sender.getName(), "Blocking block modification abilities for all non-staff.", true);
            int counter = 0;
            for (final Player player : this.server.getOnlinePlayers())
            {
                if (!plugin.sl.isStaff(player))
                {
                    final FPlayer playerdata = plugin.pl.getPlayer(player);
                    playerdata.setEditBlocked(true);
                    ++counter;
                }
            }

            msg("Blocked block modification abilities for " + counter + " players.");
            return true;
        }

        final boolean smite = args[0].equals("-s");
        if (smite)
        {
            args = (String[])ArrayUtils.subarray(args, 1, args.length);
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
            reason = StringUtils.join(args, " ", 1, args.length);
        }

        final FPlayer pd = plugin.pl.getPlayer(player2);
        if (pd.isEditBlocked())
        {
            FUtil.staffAction(sender.getName(), "Unblocking block modification abilities for " + player2.getName(), true);
            pd.setEditBlocked(false);
            msg("Unblocking block modification abilities for " + player2.getName());
            msg(player2, "Your block modification abilities have been restored.", ChatColor.RED);
        }
        else
        {
            if (plugin.sl.isStaff(player2))
            {
                msg(player2.getName() + " is a staff member, and cannot have their block edits blocked.");
                return true;
            }

            FUtil.staffAction(sender.getName(), "Blocking block modification abilities for " + player2.getName(), true);
            pd.setEditBlocked(true);

            if (smite)
            {
                Command_smite.smite(sender, player2, reason);
            }

            msg(player2, "Your block modification abilities have been blocked.", ChatColor.RED);
            msg("Blocked all block modification abilities for " + player2.getName());
        }
        return true;
    }
}