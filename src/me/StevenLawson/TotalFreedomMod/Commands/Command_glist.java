package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Ban;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Player;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TFM_UuidManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Bans or unbans any player, even those who are not logged in anymore.", usage = "/<command> <purge | <ban | unban> <username>>")
public class Command_glist extends TFM_Command
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
                if (TFM_AdminList.isSeniorAdmin(sender))
                {
                    TFM_PlayerList.purgeAll();
                    playerMsg("Purged playerbase");
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
                final TFM_Player entry = TFM_PlayerList.getEntry(TFM_UuidManager.getUniqueId(args[1]));

                if (entry == null)
                {
                    playerMsg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                    return true;
                }

                username = entry.getLastLoginName();
                ips.addAll(entry.getIps());
            }
            else
            {
                username = player.getName();
                final TFM_Player entry = TFM_PlayerList.getEntry(TFM_UuidManager.getUniqueId(player));
                ips.addAll(entry.getIps());
            }

            String mode = args[0].toLowerCase();
            if (mode.equalsIgnoreCase("ban"))
            {
                TFM_Util.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);

                final Player target = getPlayer(username, true);
                if (target != null)
                {
                    TFM_BanManager.addUuidBan(new TFM_Ban(TFM_UuidManager.getUniqueId(target), target.getName()));
                    target.kickPlayer("You have been banned by " + sender.getName() + "\n If you think you have been banned wrongly, appeal here: " + TFM_ConfigEntry.SERVER_BAN_URL.getString());
                }
                else
                {
                    TFM_BanManager.addUuidBan(new TFM_Ban(TFM_UuidManager.getUniqueId(username), username));
                }

                for (String ip : ips)
                {
                    TFM_BanManager.addIpBan(new TFM_Ban(ip, username));
                    TFM_BanManager.addIpBan(new TFM_Ban(TFM_Util.getFuzzyIp(ip), username));
                }
            }
            else if (mode.equalsIgnoreCase("unban"))
            {
                TFM_Util.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
                TFM_BanManager.unbanUuid(TFM_UuidManager.getUniqueId(username));
                for (String ip : ips)
                {

                    TFM_BanManager.unbanIp(ip);
                    TFM_BanManager.unbanIp(TFM_Util.getFuzzyIp(ip));
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
