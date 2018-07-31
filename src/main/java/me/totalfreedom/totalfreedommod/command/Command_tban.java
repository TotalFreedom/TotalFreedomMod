package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily bans a player for five minutes.", usage = "/<command> <player> [reason]", aliases = "noob")
public class Command_tban extends FreedomCommand
{

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

        String reason;
        if (args.length > 1)
        {
            reason = StringUtils.join(args, " ", 1, args.length);
        }
        else
        {
            reason = "You have been temporarily banned for 5 minutes.";
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

        FUtil.adminAction(sender.getName(), "Tempbanning: " + player.getName() + " for 5 minutes.", true);
        Ban ban = Ban.forPlayerName(username, sender, FUtil.parseDateOffset("5m"), reason);
        for (String ip : ips)
        {
            ban.addIp(ip);
        }
        plugin.bm.addBan(ban);
        player.kickPlayer(ChatColor.RED + "You have been temporarily banned for five minutes. Please read totalfreedom.me for more info.");

        plugin.pul.logPunishment(new Punishment(player.getName(), Ips.getIp(player), sender.getName(), PunishmentType.TEMPBAN, reason));

        return true;
    }
}
