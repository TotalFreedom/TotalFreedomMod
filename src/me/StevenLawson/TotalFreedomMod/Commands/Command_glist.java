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
        if (args.length != 2)
        {
            return false;
        }

        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            String username;
            List<String> ip_addresses = new ArrayList<String>();

            try
            {
                Player p = getPlayer(args[1]);

                username = p.getName();
                ip_addresses.add(p.getAddress().getAddress().getHostName());
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
            if (mode.equals("ban"))
            {
                Player p = server.getPlayerExact(username);
                if (p != null)
                {
                    p.setBanned(true);
                    p.kickPlayer("IP and username banned by Administrator.");
                }
                else
                {
                    server.getOfflinePlayer(username).setBanned(true);
                }

                for (String ip_address : ip_addresses)
                {
                    server.banIP(ip_address);
                    String[] ip_address_parts = ip_address.split("\\.");
                    server.banIP(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*");
                }
            }
            else if (mode.equals("unban"))
            {
                server.getOfflinePlayer(username).setBanned(false);

                for (String ip_address : ip_addresses)
                {
                    server.unbanIP(ip_address);
                    String[] ip_address_parts = ip_address.split("\\.");
                    server.unbanIP(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*");
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
