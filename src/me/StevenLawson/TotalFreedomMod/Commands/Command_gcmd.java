package me.StevenLawson.TotalFreedomMod.Commands;

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
            try
            {
                p = getPlayer(args[0]);
            }
            catch (CantFindPlayerException ex)
            {
                sender.sendMessage(ex.getMessage());
                return true;
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
            catch (Throwable ex)
            {
                sender.sendMessage(ChatColor.GRAY + "Error building command: " + ex.getMessage());
                return true;
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
            catch (Throwable ex)
            {
                sender.sendMessage(ChatColor.GRAY + "Error sending command: " + ex.getMessage());
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
