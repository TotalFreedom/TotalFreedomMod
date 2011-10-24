package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_tfipbanlist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("purge"))
            {
                if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
                {
                    for (String ip : Bukkit.getIPBans())
                    {
                        Bukkit.unbanIP(ip);
                    }

                    sender.sendMessage(ChatColor.GRAY + "IP Ban list has been purged.");

                    return true;
                }
                else
                {
                    sender.sendMessage(ChatColor.YELLOW + "You do not have permission to purge the IP ban list, you may only view it.");
                }
            }
        }

        List<String> ip_bans = Arrays.asList(Bukkit.getIPBans().toArray(new String[0]));
        Collections.sort(ip_bans);

        StringBuilder banned_ips = new StringBuilder();
        banned_ips.append("Banned IPs: ");
        boolean first = true;
        for (String ip : ip_bans)
        {
            if (!first)
            {
                banned_ips.append(", ");
            }
            if (ip.matches("^\\d{1,3}\\.\\d{1,3}\\.(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)$"))
            {
                first = false;
                banned_ips.append(ip.trim());
            }
        }

        sender.sendMessage(ChatColor.GRAY + banned_ips.toString());

        return true;
    }
}
