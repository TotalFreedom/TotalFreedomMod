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
@CommandParameters(description = "Toggle PVP mode for players.", usage = "/<command> [[-s] <player> [reason] | list | purge | all]", aliases = "pvpblock,pvpmode,pvpman,pvman")
public class Command_blockpvp extends FreedomCommand
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
            msg("Disabled PVP mode players:");
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isPVPBlock())
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
            FUtil.adminAction(sender.getName(), "Enabling PVP mode for all players.", true);
            FPlayer info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = plugin.pl.getPlayer(mp);
                if (info.isPVPBlock())
                {
                    info.setPVPBlock(false);
                    count++;
                }
            }
            msg("Enabling PVP mode for " + count + " players.");
            return true;
        }

        if (args[0].equals("all"))
        {
            FUtil.adminAction(sender.getName(), "Disabling PVP mode for all non-Superadmins", true);

            FPlayer playerdata;
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!plugin.al.isAdmin(player))
                {
                    playerdata = plugin.pl.getPlayer(player);
                    playerdata.setPVPBlock(true);
                    counter++;
                }
            }

            msg("Disabling PVP mode for " + counter + " players.");
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
        if (playerdata.isPVPBlock())
        {
            FUtil.adminAction(sender.getName(), "Enabling PVP mode for " + player.getName(), true);
            playerdata.setPVPBlock(false);
            msg("Enabling PVP mode for  " + player.getName());

            msg(player, "Your PVP mode have been enabled.", ChatColor.GREEN);
        }
        else
        {
            if (plugin.al.isAdmin(player))
            {
                msg(player.getName() + " is a superadmin, and his PVP mode can't be disabled.");
                return true;
            }

            FUtil.adminAction(sender.getName(), "Disabling PVP mode for " + player.getName(), true);
            playerdata.setPVPBlock(true);

            if (smite)
            {
                Command_smite.smite(player, sender);
            }

            if (reason != null)
            {
                msg(player, "Your PVP Mode has been disabled. Reason: " + reason, ChatColor.RED);
            }
            else
            {
                msg(player, "Your PVP Mode has been disabled.", ChatColor.RED);
            }

            msg("Disabled PVP mode for " + player.getName());

        }

        return true;
    }
}
