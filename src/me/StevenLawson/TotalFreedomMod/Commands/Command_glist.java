package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Ban;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerEntry;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Ban/Unban any player, even those who are not logged in anymore.", usage = "/<command> <purge | <ban | unban> <username>>")
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
                //Purge does not clear the banlist! This is not for clearing bans! This is for clearing the yaml file that stores the player/IP database!
                if (TFM_AdminList.isSeniorAdmin(sender))
                {
                    TFM_PlayerList.getInstance().purgeAll();
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
                final TFM_PlayerEntry entry = TFM_PlayerList.getInstance().getEntry(args[1]);

                if (entry == null)
                {
                    TFM_Util.playerMsg(sender, "Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                    return true;
                }

                username = entry.getLastJoinName();
                ips.addAll(entry.getIps());
            }
            else
            {
                username = player.getName();
                ips.add(player.getAddress().getAddress().getHostAddress());
            }

            String mode = args[0].toLowerCase();
            if (mode.equalsIgnoreCase("ban"))
            {
                TFM_Util.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ","), true);

                Player target = server.getPlayerExact(username);
                if (target != null)
                {
                    TFM_BanManager.getInstance().addUuidBan(new TFM_Ban(target.getUniqueId(), target.getName()));
                    target.kickPlayer("You have been banned by " + sender.getName() + "\n If you think you have been banned wrongly, appeal here: http://www.totalfreedom.boards.net");
                }
                else
                {
                    TFM_BanManager.getInstance().addUuidBan(new TFM_Ban(Bukkit.getOfflinePlayer(username).getUniqueId(), username));
                }

                for (String ip : ips)
                {
                    TFM_BanManager.getInstance().addIpBan(new TFM_Ban(ip, username));
                    String[] ip_address_parts = ip.split("\\.");
                    TFM_BanManager.getInstance().addIpBan(new TFM_Ban(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*", username));
                }
            }
            else if (mode.equalsIgnoreCase("unban") || mode.equalsIgnoreCase("pardon"))
            {
                TFM_Util.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ","), true);

                TFM_BanManager.getInstance().unbanUuid(Bukkit.getOfflinePlayer(username).getUniqueId());

                for (String ip : ips)
                {
                    TFM_BanManager.getInstance().unbanIp(ip);
                    TFM_BanManager.getInstance().unbanIp(TFM_Util.getFuzzyIp(ip));
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
