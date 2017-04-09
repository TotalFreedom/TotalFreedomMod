package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Blocks all block placing for player with brute force.", usage = "/<command> [[-s] <player> [reason] | list | purge | all]", aliases = "editmute")
public class Command_blockedit extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("list"))
        {
            msg("Blocked block edits players:");
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isEditBlock())
                {
                    msg("- " + mp.getName());
                    count++;
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
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isEditBlock())
                {
                    info.setEditBlocked(false);
                    count++;
                }
            }
            msg("Unblocked all block edit for " + count + " players.");
            return true;
        }

        if (args[0].equals("all"))
        {
            FUtil.adminAction(sender.getName(), "Blocking block edits for all non-Superadmins", true);

            FPlayer playerdata;
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!plugin.al.isAdmin(player))
                {
                    playerdata = plugin.pl.getPlayer(player);
                    playerdata.setEditBlocked(true);
                    counter++;
                }
            }

            msg("Blocked all block edit for " + counter + " players.");
            return true;
        }

        // -s option (smite)
        boolean smite = args[0].equals("-s");
        if (smite)
        {
            args = ArrayUtils.subarray(args, 1, args.length);

            if (args.length < 1)
            {
                return false;
            }
        }

        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        String reason = null;
        if (args.length > 1)
        {
            reason = StringUtils.join(args, " ", 1, args.length);
        }

        FPlayer playerdata = plugin.pl.getPlayer(player);
        if (playerdata.isEditBlock())
        {
            FUtil.adminAction(sender.getName(), "Unblocking all block edits for " + player.getName(), true);
            playerdata.setEditBlocked(false);
            msg("Unblocking all block edits for " + player.getName());

            msg(player, "You block edits have been unblocked.", ChatColor.RED);
        }
        else
        {
            if (plugin.al.isAdmin(player))
            {
                msg(player.getName() + " is a superadmin, and his block edits can't be blocked .");
                return true;
            }

            FUtil.adminAction(sender.getName(), "Blocking block edits for " + player.getName(), true);
            playerdata.setEditBlocked(true);

            if (smite)
            {
                Command_smite.smite(player, sender);
            }

            if (reason != null)
            {
                msg(player, "You block edits have been blocked. Reason: " + reason, ChatColor.RED);
            }
            else
            {
                msg(player, "You block edits have been blocked.", ChatColor.RED);
            }

            msg("Blocked all block edits for " + player.getName());

        }

        return true;
    }
}
