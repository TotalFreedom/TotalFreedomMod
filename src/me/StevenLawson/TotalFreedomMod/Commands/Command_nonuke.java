package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_nonuke extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            if (args.length < 1)
            {
                return false;
            }

            if (args.length >= 2)
            {
                try
                {
                    TotalFreedomMod.nukeMonitorRange = Math.max(1.0, Math.min(500.0, Double.parseDouble(args[1])));
                }
                catch (NumberFormatException nfex)
                {
                }
            }

            if (args.length >= 3)
            {
                try
                {
                    TotalFreedomMod.nukeMonitorCountBreak = Math.max(1, Math.min(500, Integer.parseInt(args[2])));
                }
                catch (NumberFormatException nfex)
                {
                }
            }

            if (args[0].equalsIgnoreCase("on"))
            {
                TotalFreedomMod.nukeMonitor = true;
                sender.sendMessage(ChatColor.GRAY + "Nuke monitor is enabled.");
                sender.sendMessage(ChatColor.GRAY + "Anti-freecam range is set to " + TotalFreedomMod.nukeMonitorRange + " blocks.");
                sender.sendMessage(ChatColor.GRAY + "Block throttle rate is set to " + TotalFreedomMod.nukeMonitorCountBreak + " blocks destroyed per 5 seconds.");
            }
            else
            {
                TotalFreedomMod.nukeMonitor = false;
                sender.sendMessage("Nuke monitor is disabled.");
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
