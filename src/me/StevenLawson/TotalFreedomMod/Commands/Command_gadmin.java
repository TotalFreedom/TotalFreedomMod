package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
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

        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            if (mode.equals("list"))
            {
                sender.sendMessage(ChatColor.GRAY + "[ Real Name ] : [ Display Name ] - Hash:");
            }

            for (Player p : Bukkit.getOnlinePlayers())
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
                        Bukkit.getOfflinePlayer(p.getName()).setBanned(true);
                        TFM_Util.tfm_broadcastMessage(String.format("Banning Name: %s.", p.getName()), ChatColor.RED);
                        p.kickPlayer("Username banned by Administrator.");
                    }
                    else if (mode.equals("ipban"))
                    {
                        String user_ip = p.getAddress().getAddress().toString().replaceAll("/", "").trim();
                        TFM_Util.tfm_broadcastMessage(String.format("Banning IP: %s.", p.getName(), user_ip), ChatColor.RED);
                        Bukkit.banIP(user_ip);
                        p.kickPlayer("IP address banned by Administrator.");
                    }
                    else if (mode.equals("ban"))
                    {
                        String user_ip = p.getAddress().getAddress().toString().replaceAll("/", "").trim();
                        TFM_Util.tfm_broadcastMessage(String.format("Banning Name: %s, IP: %s.", p.getName(), user_ip), ChatColor.RED);
                        Bukkit.banIP(user_ip);
                        Bukkit.getOfflinePlayer(p.getName()).setBanned(true);
                        p.kickPlayer("IP and username banned by Administrator.");
                    }
                    else if (mode.equals("op"))
                    {
                        TFM_Util.tfm_broadcastMessage(String.format("(%s: Opping %s)", sender.getName(), p.getName()), ChatColor.GRAY);
                        p.setOp(false);
                        p.sendMessage(TotalFreedomMod.YOU_ARE_OP);
                    }
                    else if (mode.equals("deop"))
                    {
                        TFM_Util.tfm_broadcastMessage(String.format("(%s: De-opping %s)", sender.getName(), p.getName()), ChatColor.GRAY);
                        p.setOp(false);
                        p.sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);
                    }
                    else if (mode.equals("ci"))
                    {
                        p.getInventory().clear();
                    }
                    else if (mode.equals("fr"))
                    {
                        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p, plugin);
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
