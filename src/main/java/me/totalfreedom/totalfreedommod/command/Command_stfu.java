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
@CommandParameters(description = "Mutes a player with brute force.", usage = "/<command> [[-s] <player> [reason] | list | purge | all]", aliases = "mute")
public class Command_stfu extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            msg("Muted players:");
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isMuted())
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

        if (args[0].equalsIgnoreCase("purge"))
        {
            FUtil.adminAction(sender.getName(), "Unmuting all players.", true);
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isMuted())
                {
                    info.setMuted(false);
                    count++;
                }
            }
            msg("Unmuted " + count + " players.");
            return true;
        }

        if (args[0].equalsIgnoreCase("all"))
        {
            FUtil.adminAction(sender.getName(), "Muting all non-Superadmins", true);

            FPlayer playerdata;
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!plugin.al.isAdmin(player))
                {
                    playerdata = plugin.pl.getPlayer(player);
                    playerdata.setMuted(true);
                    counter++;
                }
            }

            msg("Muted " + counter + " players.");
            return true;
        }

        boolean smite = args[0].equals("-s");
        if (smite)
        {
            args = ArrayUtils.subarray(args, 1, args.length);
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
        if (playerdata.isMuted())
        {
            FUtil.adminAction(sender.getName(), "Unmuting " + player.getName(), true);
            playerdata.setMuted(false);
            msg("Unmuted " + player.getName());

            msg(player, "You have been unmuted.", ChatColor.RED);
        }
        else
        {
            if (plugin.al.isAdmin(player))
            {
                msg(player.getName() + " is a superadmin, and can't be muted.");
                return true;
            }

            FUtil.adminAction(sender.getName(), "Muting " + player.getName(), true);
            playerdata.setMuted(true);

            if (smite)
            {
                Command_smite.smite(player);
            }

            if (reason != null)
            {
                msg(player, "You have been muted. Reason: " + reason, ChatColor.RED);
            }
            else
            {
                msg(player, "You have been muted.", ChatColor.RED);
            }

            msg("Muted " + player.getName());

        }

        return true;
    }
}
