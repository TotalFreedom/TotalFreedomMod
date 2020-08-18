package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Kick the specified player.", usage = "/<command> <player> [reason] [-q]")
public class Command_kick extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        boolean silent = false;

        String reason = null;
        if (args[args.length - 1].equalsIgnoreCase("-q"))
        {
            silent = true;
            FLog.debug("silent");

            if (args.length >= 2)
            {
                FLog.debug("set reason (silent)");
                reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length - 1), " ");
            }
        }
        else if (args.length > 1)
        {
            FLog.debug("set reason");
            reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        }

        StringBuilder builder = new StringBuilder()
                .append(ChatColor.RED).append("You have been kicked from the server.")
                .append("\n").append(ChatColor.RED).append("Kicked by: ").append(ChatColor.GOLD).append(sender.getName());

        if (reason != null)
        {
            builder.append("\n").append(ChatColor.RED).append("Reason: ").append(ChatColor.GOLD).append(reason);
        }

        if (!silent)
        {
            if (reason != null)
            {
                FUtil.staffAction(sender.getName(), "Kicking " + player.getName() + " - Reason: " + reason, true);
            }
            else
            {
                FUtil.staffAction(sender.getName(), "Kicking " + player.getName(), true);
            }
        }
        else
        {
            msg("Kicked " + player.getName() + " quietly.");
        }

        player.kickPlayer(builder.toString());

        plugin.pul.logPunishment(new Punishment(player.getName(), FUtil.getIp(player), sender.getName(), PunishmentType.KICK, reason));

        return true;
    }
}
