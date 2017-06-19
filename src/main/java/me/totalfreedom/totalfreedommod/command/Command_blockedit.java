package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Allows or disallows players to modify blocks.", usage = "/<command> [<player> [reason] | list | purge | all]")
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
            msg("Block modification is disallowed for these players:");
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isBlockEditsBlocked())
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
            FUtil.adminAction(sender.getName(), "Allowing all players to modify blocks", true);
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isBlockEditsBlocked())
                {
                    info.setBlockEditsBlocked(false);
                    count++;
                }
            }
            msg("Allowed " + count + " players to modify blocks again.");
            return true;
        }

        if (args[0].equals("all"))
        {
            FUtil.adminAction(sender.getName(), "Disallowing all players to modify blocks", true);

            FPlayer playerdata;
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!plugin.al.isAdmin(player))
                {
                    playerdata = plugin.pl.getPlayer(player);
                    playerdata.setBlockEditsBlocked(true);
                    counter++;
                }
            }

            msg("Disallowed " + counter + " players to modify blocks.");
            return true;
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
        if (playerdata.isBlockEditsBlocked())
        {
            FUtil.adminAction(sender.getName(), "Allowing " + player.getName() + " to modify blocks again", true);
            playerdata.setBlockEditsBlocked(false);
            msg("Allowed " + player.getName() + " to modify blocks again.");

            msg(player, "You can now modify blocks again!", ChatColor.RED);
        }
        else
        {
            if (plugin.al.isAdmin(player))
            {
                msg(player.getName() + " is an admin, and cannot be disallowed from modifying blocks.");
                return true;
            }

            FUtil.adminAction(sender.getName(), "Disallowing " + player.getName() + " to modify blocks", true);
            playerdata.setBlockEditsBlocked(true);

            if (reason != null)
            {
                msg(player, "You have been disallowed from modifying blocks. Reason: " + reason, ChatColor.RED);
            }
            else
            {
                msg(player, "You have been disallowed from modifying blocks.", ChatColor.RED);
            }

            msg("Disallowed " + player.getName() + " to modify blocks.");
        }

        return true;
    }
}
