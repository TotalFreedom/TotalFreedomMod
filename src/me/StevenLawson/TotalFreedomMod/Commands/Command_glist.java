package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_UserList;
import me.StevenLawson.TotalFreedomMod.TFM_UserList.TFM_UserListEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_glist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender))
        {
            if (args.length == 1)
            {
                if (args[0].equalsIgnoreCase("purge"))
                {
                    TFM_UserList.getInstance(plugin).purge();
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (args.length != 2)
            {
                return false;
            }

            String username;
            List<String> ip_addresses = new ArrayList<String>();

            try
            {
                Player p = getPlayer(args[1]);

                username = p.getName();
                ip_addresses.add(p.getAddress().getAddress().getHostAddress());
            }
            catch (CantFindPlayerException ex)
            {
                TFM_UserListEntry entry = TFM_UserList.getInstance(plugin).getEntry(args[1]);

                if (entry == null)
                {
                    sender.sendMessage("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                    return true;
                }

                username = entry.getUsername();
                ip_addresses = entry.getIpAddresses();
            }

            String mode = args[0].toLowerCase();
            if (mode.equalsIgnoreCase("ban"))
            {
                TFM_Util.adminAction(sender.getName(), "Banning " + username + " and IPs: " + TFM_Util.implodeStringList(",", ip_addresses), true);

                Player p = server.getPlayerExact(username);
                if (p != null)
                {
                    //p.setBanned(true);
                    TFM_Util.banUsername(p.getName(), null, null, null);
                    p.kickPlayer("IP and username banned by Administrator.");
                }
                else
                {
                    //server.getOfflinePlayer(username).setBanned(true);
                    TFM_Util.banUsername(username, null, null, null);
                }

                for (String ip_address : ip_addresses)
                {
                    //server.banIP(ip_address);
                    TFM_Util.banIP(ip_address, null, null, null);
                    String[] ip_address_parts = ip_address.split("\\.");
                    //server.banIP(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*");
                    TFM_Util.banIP(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*", null, null, null);
                }
            }
            else if (mode.equalsIgnoreCase("unban"))
            {
                TFM_Util.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + TFM_Util.implodeStringList(",", ip_addresses), true);

                //server.getOfflinePlayer(username).setBanned(false);
                TFM_Util.unbanUsername(username);

                for (String ip_address : ip_addresses)
                {
                    //server.unbanIP(ip_address);
                    TFM_Util.unbanIP(ip_address);
                    String[] ip_address_parts = ip_address.split("\\.");
                    //server.unbanIP(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*");
                    TFM_Util.unbanIP(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*");
                }
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
