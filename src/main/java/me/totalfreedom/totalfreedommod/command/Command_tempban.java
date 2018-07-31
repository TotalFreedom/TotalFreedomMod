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
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily ban someone.", usage = "/<command> [playername] [duration] [reason]")
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

        final String username;
        final List<String> ips = new ArrayList<>();

        final Player player = getPlayer(args[0]);
        final PlayerData entry = plugin.pl.getData(args[0]);


        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        username = entry.getUsername();
        ips.addAll(entry.getIps());
        final StringBuilder message = new StringBuilder("Temporarily banned " + player.getName());

        Date expires = FUtil.parseDateOffset("30m");
        if (args.length >= 2)
        {
            Date parsed_offset = FUtil.parseDateOffset(args[1]);
            if (parsed_offset != null)
            {
                expires = parsed_offset;
            }
        }
        message.append(" until ").append(date_format.format(expires));

        String reason = "Banned by " + sender.getName();
        if (args.length >= 3)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ") + " (" + sender.getName() + ")";
            message.append(", Reason: \"").append(reason).append("\"");
        }

        // strike with lightning effect:
        final Location targetPos = player.getLocation();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                targetPos.getWorld().strikeLightningEffect(strike_pos);
            }
        }

        FUtil.adminAction(sender.getName(), message.toString(), true);

        Ban ban = Ban.forPlayerName(username, sender, expires, reason);
        for (String ip : ips)
        {
            ban.addIp(ip);
        }
        plugin.bm.addBan(ban);
        player.kickPlayer(sender.getName() + " - " + message.toString());

        plugin.pul.logPunishment(new Punishment(player.getName(), Ips.getIp(player), sender.getName(), PunishmentType.TEMPBAN, reason));

        return true;
    }
}
