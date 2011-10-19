package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_gcmd extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            if (args.length < 2)
            {
                return false;
            }

            Player p;
            List<Player> matches = Bukkit.matchPlayer(args[0]);
            if (matches.isEmpty())
            {
                sender.sendMessage(ChatColor.GRAY + "Can't find user " + args[0]);
                return true;
            }
            else
            {
                p = matches.get(0);
            }

            String outcommand = "";
            try
            {
                StringBuilder outcommand_bldr = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                {
                    outcommand_bldr.append(args[i]).append(" ");
                }
                outcommand = outcommand_bldr.toString().trim();
            }
            catch (Exception cmdex)
            {
                sender.sendMessage(ChatColor.GRAY + "Error building command: " + cmdex.getMessage());
            }

            try
            {
                sender.sendMessage(ChatColor.GRAY + "Sending command as " + p.getName() + ": " + outcommand);
                if (Bukkit.getServer().dispatchCommand(p, outcommand))
                {
                    sender.sendMessage(ChatColor.GRAY + "Command sent.");
                }
                else
                {
                    sender.sendMessage(ChatColor.GRAY + "Unknown error sending command.");
                }
            }
            catch (Exception cmdex)
            {
                sender.sendMessage(ChatColor.GRAY + "Error sending command: " + cmdex.getMessage());
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
