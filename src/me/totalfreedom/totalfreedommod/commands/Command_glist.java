package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.FBan;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Bans or unbans any player, even those who are not logged in anymore.", usage = "/<command> <purge | <ban | unban> <username>>")
public class Command_glist extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("purge"))
            {
                if (getAdmin(sender).getRank() == PlayerRank.SENIOR_ADMIN)
                {
                    plugin.pl.purgeAllData();
                    playerMsg("Purged playerbase.");
                }
                else
                {
                    playerMsg("Only Senior Admins may purge the userlist.");
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        else if (args.length == 2)
        {
            String username;
            final List<String> ips = new ArrayList<String>();

            final Player player = getPlayer(args[1]);

            if (player == null)
            {
                final PlayerData entry = plugin.pl.getData(args[1]);

                if (entry == null)
                {
                    playerMsg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                    return true;
                }

                username = entry.getUsername();
                ips.addAll(entry.getIps());
            }
            else
            {
                username = player.getName();
                final PlayerData entry = plugin.pl.getData(player);
                ips.addAll(entry.getIps());
            }

            String mode = args[0].toLowerCase();
            if (mode.equalsIgnoreCase("ban"))
            {
                FUtil.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);

                final Player target = getPlayer(username, true);
                if (target != null)
                {
                    target.kickPlayer("You have been banned by " + sender.getName() + "\n If you think you have been banned wrongly, appeal here: " + ConfigEntry.SERVER_BAN_URL.getString());
                }

                FBan ban = FBan.forPlayerFuzzy(player, sender, null, null);
                for (String ip : ips)
                {
                    ban.addIp(ip);
                    ban.addIp(FUtil.getFuzzyIp(ip));
                }
                plugin.bm.addBan(ban);
            }
            else if (mode.equalsIgnoreCase("unban"))
            {
                FUtil.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
                plugin.bm.removeBan(plugin.bm.getByUsername(username));
                for (String ip : ips)
                {
                    FBan ban = plugin.bm.getByIp(ip);
                    if (ban != null)
                    {
                        plugin.bm.removeBan(ban);
                    }
                }
            }
            else
            {
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
