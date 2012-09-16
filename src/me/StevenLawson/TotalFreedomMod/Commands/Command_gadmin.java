package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_gadmin extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        String mode = args[0].toLowerCase();

        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender))
        {
            if (mode.equals("list"))
            {
                sender.sendMessage(ChatColor.GRAY + "[ Real Name ] : [ Display Name ] - Hash:");
            }

            for (Player p : server.getOnlinePlayers())
            {
                String hash = p.getUniqueId().toString().substring(0, 4);
                if (mode.equals("list"))
                {
                    sender.sendMessage(ChatColor.GRAY + String.format("[ %s ] : [ %s ] - %s",
                            p.getName(),
                            ChatColor.stripColor(p.getDisplayName()),
                            hash));
                }
                else if (hash.equalsIgnoreCase(args[1]))
                {
                    if (mode.equals("kick"))
                    {
                        p.kickPlayer("Kicked by Administrator");
                    }
                    else if (mode.equals("nameban"))
                    {
                        //server.getOfflinePlayer(p.getName()).setBanned(true);
                        TFM_Util.banUsername(p.getName(), null, null, null);
                        TFM_Util.bcastMsg(String.format("Banning Name: %s.", p.getName()), ChatColor.RED);
                        p.kickPlayer("Username banned by Administrator.");
                    }
                    else if (mode.equals("ipban"))
                    {
                        String user_ip = p.getAddress().getAddress().getHostAddress();
                        String[] ip_parts = user_ip.split("\\.");
                        if (ip_parts.length == 4)
                        {
                            user_ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
                        }
                        TFM_Util.bcastMsg(String.format("Banning IP: %s.", p.getName(), user_ip), ChatColor.RED);
                        //server.banIP(user_ip);
                        TFM_Util.banIP(user_ip, null, null, null);
                        p.kickPlayer("IP address banned by Administrator.");
                    }
                    else if (mode.equals("ban"))
                    {
                        String user_ip = p.getAddress().getAddress().getHostAddress();
                        String[] ip_parts = user_ip.split("\\.");
                        if (ip_parts.length == 4)
                        {
                            user_ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
                        }
                        TFM_Util.bcastMsg(String.format("Banning Name: %s, IP: %s.", p.getName(), user_ip), ChatColor.RED);
                        //server.banIP(user_ip);
                        //server.getOfflinePlayer(p.getName()).setBanned(true);
                        TFM_Util.banIP(user_ip, null, null, null);
                        TFM_Util.banUsername(p.getName(), null, null, null);
                        p.kickPlayer("IP and username banned by Administrator.");
                    }
                    else if (mode.equals("op"))
                    {
                        TFM_Util.bcastMsg(String.format("(%s: Opping %s)", sender.getName(), p.getName()), ChatColor.GRAY);
                        p.setOp(false);
                        p.sendMessage(TotalFreedomMod.YOU_ARE_OP);
                    }
                    else if (mode.equals("deop"))
                    {
                        TFM_Util.bcastMsg(String.format("(%s: De-opping %s)", sender.getName(), p.getName()), ChatColor.GRAY);
                        p.setOp(false);
                        p.sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);
                    }
                    else if (mode.equals("ci"))
                    {
                        p.getInventory().clear();
                    }
                    else if (mode.equals("fr"))
                    {
                        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);
                        playerdata.setFrozen(!playerdata.isFrozen());

                        sender.sendMessage(ChatColor.AQUA + p.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                        p.sendMessage(ChatColor.AQUA + "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                    }

                    return true;
                }
            }

            if (!mode.equals("list"))
            {
                sender.sendMessage(ChatColor.RED + "Invalid hash.");
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
