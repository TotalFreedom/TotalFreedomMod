package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.punishments.Punishment;
import me.totalfreedom.totalfreedommod.punishments.PunishmentType;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Bans or unbans any player, even those who are not logged in anymore.", usage = "/<command> <ban <username> [reason] | unban <username> | banip <ip> <reason> | unbanip <ip> | nameban <name> | unbanname <name>>")
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
        if (!usingIp)
        {
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
                username = entry.getUsername();
                ips.addAll(entry.getIps());
            }
        }
        switch (args[0])
        {
            case "ban":
            case "gtfo":
                if (usingIp)
                {
                    msg("Please specify a player, not an ip.");
                    return true;
                }
                final String playerBanReason = args.length > 2 ? StringUtils.join(args, " ", 2, args.length) : null;
                Ban playerBan = Ban.forPlayerName(username, sender, null, playerBanReason);
                for (String ip : ips)
                {
                    playerBan.addIp(ip);
                    playerBan.addIp(FUtil.getFuzzyIp(ip));
                }
                FUtil.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);

                plugin.bm.addBan(playerBan);

                if (player != null)
                {
                    player.kickPlayer(playerBan.bakeKickMessage());
                }

                plugin.pul.logPunishment(new Punishment(username, ips.get(0), sender.getName(), PunishmentType.BAN, null));

                return true;
            case "unban":
            case "pardon":
                if (usingIp)
                {
                    msg("Please specify a player, not an ip.");
                    return true;
                }
                FUtil.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
                plugin.bm.removeBan(plugin.bm.getByUsername(username));

                for (String ip : ips)
                {
                    Ban playerUnban = plugin.bm.getByIp(ip);
                    if (playerUnban != null)
                    {
                        plugin.bm.removeBan(playerUnban);
                    }
                    playerUnban = plugin.bm.getByIp(FUtil.getFuzzyIp(ip));
                    if (playerUnban != null)
                    {
                        plugin.bm.removeBan(playerUnban);
                    }
                }
                return true;
            case "nameban":
            case "banname":
                if (usingIp)
                {
                    msg("Please specify a name, not an ip.");
                    return true;
                }
                final String nameBanReason = args.length > 2 ? StringUtils.join(args, " ", 2, args.length) : null;
                Ban nameBan = Ban.forPlayerName(username, sender, null, nameBanReason);
                FUtil.adminAction(sender.getName(), "Banning IGN: " + username, true);
                plugin.bm.addBan(nameBan);

                if (player != null)
                {
                    player.kickPlayer(nameBan.bakeKickMessage());
                }

                return true;
            case "unbanname":
            case "nameunban":
                if (usingIp)
                {
                    msg("Please specify a name, not an ip.");
                    return true;
                }
                FUtil.adminAction(sender.getName(), "Unbanning IGN: " + username, true);
                plugin.bm.removeBan(plugin.bm.getByUsername(username));
                return true;
            case "banip":
            case "ipban":
                if (!usingIp)
                {
                    msg("Please specify an IP.");
                    return true;
                }

                final String ipBanReason = args.length > 2 ? StringUtils.join(args, " ", 2, args.length) : null;
                Ban ipBan = Ban.forPlayerIp(banIp, sender, null, ipBanReason);
                plugin.bm.addBan(ipBan);
                FUtil.adminAction(sender.getName(), "Banning IP: " + banIp, true);
                return true;
            case "unbanip":
            case "pardonip":
                if (!usingIp)
                {
                    msg("Please specify an IP.");
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Unbanning IP: " + banIp, true);
                Ban ipUnban = plugin.bm.getByIp(banIp);
                if (ipUnban != null)
                {
                    plugin.bm.removeBan(ipUnban);
                    plugin.bm.unbanIp(banIp);
                }
                ipUnban = plugin.bm.getByIp(FUtil.getFuzzyIp(banIp));
                if (ipUnban != null)
                {
                    plugin.bm.removeBan(ipUnban);
                    plugin.bm.unbanIp(banIp);
                }
                return true;
            default:
                return false;
        }
    }
}

