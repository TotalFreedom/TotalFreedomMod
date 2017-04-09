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
@CommandParameters(description = "Bans or unbans any player, even those who are not logged in anymore.", usage = "/<command> <purge | ban <username> [reason] | unban <username> | banip <ip> <reason> | unbanip <ip>>")
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

        String username = null;
        final List<String> ips = new ArrayList<>();
        boolean usingIp = false;
        String banIp = null;
        if (args[1].matches("^([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))$") || args[1].matches("^([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([*])\\.([*])$"))
        {
            usingIp = true;
            banIp = args[1];
        }
        final Player player = getPlayer(args[1]);

        if (player == null && !usingIp)
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

        else if (player != null && !usingIp)
        {
            final PlayerData entry = plugin.pl.getData(player);
            username = entry.getUsername();
            ips.addAll(entry.getIps());
        }

        if ("ban".equals(args[0]))
        {
            if (usingIp)
            {
                msg("Please specify a player, not an ip.");
                return true;
            }
            final String reason = args.length > 2 ? StringUtils.join(args, " ", 2, args.length) : null;
            Ban ban = Ban.forPlayerName(username, sender, null, reason);
            for (String ip : ips)
            {
                ban.addIp(ip);
                ban.addIp(FUtil.getFuzzyIp(ip));
            }
            FUtil.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);

            plugin.bm.addBan(ban);

            if (player != null)
            {
                player.kickPlayer(ban.bakeKickMessage());
            }
            return true;
        }

        if ("unban".equals(args[0]))
        {
            if (usingIp)
            {
                msg("Please specify a player, not an ip.");
                return true;
            }
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

        if ("banip".equals(args[0]) || "ipban".equals(args[0]))
        {
            if (banIp == null)
            {
                msg("Please specify an IP");
                return true;
            }

            final String reason = args.length > 2 ? StringUtils.join(args, " ", 2, args.length) : null;
            Ban ban = Ban.forPlayerIp(banIp, sender, null, reason);
            plugin.bm.addBan(ban);
            FUtil.adminAction(sender.getName(), "Banning IP: " + banIp, true);
            return true;
        }

        if ("unbanip".equals(args[0]) || "pardonip".equals(args[0]))
        {
            if (banIp == null)
            {
                msg("Please specify an IP");
                return true;
            }

            FUtil.adminAction(sender.getName(), "Unbanning IP: " + banIp, true);
            Ban ban = plugin.bm.getByIp(banIp);
            if (ban != null)
            {
                plugin.bm.removeBan(ban);
                plugin.bm.unbanIp(banIp);
            }
            ban = plugin.bm.getByIp(FUtil.getFuzzyIp(banIp));
            if (ban != null)
            {
                plugin.bm.removeBan(ban);
                plugin.bm.unbanIp(banIp);
            }
            return true;
        }

        return true;
    }

}