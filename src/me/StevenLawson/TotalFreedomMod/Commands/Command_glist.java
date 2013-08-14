package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_UserList;
import me.StevenLawson.TotalFreedomMod.TFM_UserList.TFM_UserListEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang.StringUtils;
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
                if (TFM_SuperadminList.isSeniorAdmin(sender))
                {
                    TFM_UserList.getInstance(plugin).purge();
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
            List<String> ip_addresses = new ArrayList<String>();

            try
            {
                Player player = getPlayer(args[1]);

                username = player.getName();
                ip_addresses.add(player.getAddress().getAddress().getHostAddress());
            }
            catch (PlayerNotFoundException ex)
            {
                TFM_UserListEntry entry = TFM_UserList.getInstance(plugin).getEntry(args[1]);

                if (entry == null)
                {
                    TFM_Util.playerMsg(sender, "Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                    return true;
                }

                username = entry.getUsername();
                ip_addresses = entry.getIpAddresses();
            }

            String mode = args[0].toLowerCase();
            if (mode.equalsIgnoreCase("ban"))
            {
                TFM_Util.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ip_addresses, ","), true);

                Player player = server.getPlayerExact(username);
                if (player != null)
                {
                    TFM_ServerInterface.banUsername(player.getName(), null, null, null);
                    player.kickPlayer("You have been banned by " + sender.getName() + "\n If you think you have been banned wrongly, appeal here: http://www.totalfreedom.boards.net");
                }
                else
                {
                    TFM_ServerInterface.banUsername(username, null, null, null);
                }

                for (String ip_address : ip_addresses)
                {
                    TFM_ServerInterface.banIP(ip_address, null, null, null);
                    String[] ip_address_parts = ip_address.split("\\.");
                    TFM_ServerInterface.banIP(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*", null, null, null);
                }
            }
            else if (mode.equalsIgnoreCase("unban") || mode.equalsIgnoreCase("pardon"))
            {
                TFM_Util.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ip_addresses, ","), true);

                TFM_ServerInterface.unbanUsername(username);

                for (String ip_address : ip_addresses)
                {
                    TFM_ServerInterface.unbanIP(ip_address);
                    String[] ip_address_parts = ip_address.split("\\.");
                    TFM_ServerInterface.unbanIP(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*");
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
