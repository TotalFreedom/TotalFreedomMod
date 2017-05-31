package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Bans or unbans any player, even those who are not logged in anymore.", usage = "/<command> <purge | ban <username> [reason] | unban <username>>")
public class Command_glist extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        if (args.length == 1)
        {
            if ("purge".equals(args[0]))
            {
                checkRank(Rank.SENIOR_ADMIN);
                plugin.pl.purgeAllData();
                msg("Purged playerbase.");

                return true;
            }

            return false;
        }

        if (args.length < 2)
        {
            return false;
        }

        String username;
        final List<String> ips = new ArrayList<>();

        final Player player = getPlayer(args[1]);
        if (player == null)
        {
            final PlayerData entry = plugin.pl.getData(args[1]);

            if (entry == null)
            {
                msg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                return true;
            }

            username = entry.getUsername();
            ips.addAll(entry.getIps());
        }
        else
        {
            final PlayerData entry = plugin.pl.getData(player);
            username = player.getName();
            ips.addAll(entry.getIps());
        }

        if ("ban".equals(args[0]))
        {
            FUtil.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);

            final String reason = args.length > 2 ? StringUtils.join(args, " ", 2, args.length) : null;

            Ban ban = Ban.forPlayerName(username, sender, null, reason);
            for (String ip : ips)
            {
                ban.addIp(ip);
                ban.addIp(FUtil.getFuzzyIp(ip));
            }
            plugin.bm.addBan(ban);

            if (player != null)
            {
                player.kickPlayer(ban.bakeKickMessage());
            }
            return true;
        }

        if ("unban".equals(args[0]))
        {
            FUtil.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
            plugin.bm.removeBan(plugin.bm.getByUsername(username));

            for (String ip : ips)
            {
                Ban ban = plugin.bm.getByIp(ip);
                if (ban != null)
                {
                    plugin.bm.removeBan(ban);
                }
                ban = plugin.bm.getByIp(FUtil.getFuzzyIp(ip));
                if (ban != null)
                {
                    plugin.bm.removeBan(ban);
                }
            }

            return true;
        }

        return false;
    }
}
