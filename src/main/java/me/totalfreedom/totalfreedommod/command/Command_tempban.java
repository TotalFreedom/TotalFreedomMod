package me.totalfreedom.totalfreedommod.command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily ban someone.", usage = "/<command> [-q] <username> [duration] [reason]")
public class Command_tempban extends FreedomCommand
{

    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        boolean quiet = args[0].equalsIgnoreCase("-q");
        if (quiet)
        {
            args = org.apache.commons.lang3.ArrayUtils.subarray(args, 1, args.length);

            if (args.length < 1)
            {
                return false;
            }
        }

        final String username;
        final List<String> ips = new ArrayList<>();

        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            final PlayerData entry = plugin.pl.getData(args[0]);

            if (entry == null)
            {
                msg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                return true;
            }

            username = entry.getName();
            ips.addAll(entry.getIps());
        }
        else
        {
            final PlayerData entry = plugin.pl.getData(player);
            username = player.getName();
            ips.addAll(entry.getIps());
        }

        final StringBuilder message = new StringBuilder("Temporarily banned " + player.getName());

        Date expires = FUtil.parseDateOffset("30m");
        message.append(" until ").append(date_format.format(expires));

        String reason = null;
        if (args.length >= 2)
        {
            Date parsed_offset = FUtil.parseDateOffset(args[1]);
            reason = StringUtils.join(ArrayUtils.subarray(args, parsed_offset == null ? 1 : 2, args.length), " ") + " (" + sender.getName() + ")";
            if (parsed_offset != null)
            {
                expires = parsed_offset;
            }
            message.append(", Reason: \"").append(reason).append("\"");
        }

        if (!quiet)
        {
            // Strike with lightning
            final Location targetPos = player.getLocation();
            for (int x = -1; x <= 1; x++)
            {
                for (int z = -1; z <= 1; z++)
                {
                    final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                    targetPos.getWorld().strikeLightningEffect(strike_pos);
                }
            }

            FUtil.staffAction(sender.getName(), message.toString(), true);
        }
        else
        {
            msg("Quietly temporarily banned " + player.getName() + ".");
        }


        Ban ban;

        if (player != null)
        {
            ban = Ban.forPlayer(player, sender, null, reason);
        }
        else
        {
            ban = Ban.forPlayerName(username, sender, null, reason);
        }

        for (String ip : ips)
        {
            ban.addIp(ip);
        }
        plugin.bm.addBan(ban);
        player.kickPlayer(ban.bakeKickMessage());
        plugin.pul.logPunishment(new Punishment(player.getName(), FUtil.getIp(player), sender.getName(), PunishmentType.TEMPBAN, reason));
        return true;
    }
}