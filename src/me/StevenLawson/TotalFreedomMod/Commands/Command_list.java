package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_list extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        StringBuilder onlineStats = new StringBuilder();
        StringBuilder onlineUsers = new StringBuilder();

        if (senderIsConsole)
        {
            onlineStats.append(String.format("There are %d out of a maximum %d players online.", Bukkit.getOnlinePlayers().length, Bukkit.getMaxPlayers()));

            onlineUsers.append("Connected players: ");
            boolean first = true;
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    onlineUsers.append(", ");
                }

                if (sender.getName().equalsIgnoreCase("remotebukkit"))
                {
                    onlineUsers.append(p.getName());
                }
                else
                {
                    if (p.isOp())
                    {
                        onlineUsers.append("[OP]").append(p.getName());
                    }
                    else
                    {
                        onlineUsers.append(p.getName());
                    }
                }
            }
        }
        else
        {
            onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(Bukkit.getOnlinePlayers().length);
            onlineStats.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(Bukkit.getMaxPlayers());
            onlineStats.append(ChatColor.BLUE).append(" players online.");

            onlineUsers.append("Connected players: ");
            boolean first = true;
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    onlineUsers.append(", ");
                }

                if (p.isOp())
                {
                    onlineUsers.append(ChatColor.RED).append(p.getName());
                }
                else
                {
                    onlineUsers.append(p.getName());
                }

                onlineUsers.append(ChatColor.WHITE);
            }
        }

        sender.sendMessage(onlineStats.toString());
        sender.sendMessage(onlineUsers.toString());

        return true;
    }
}
