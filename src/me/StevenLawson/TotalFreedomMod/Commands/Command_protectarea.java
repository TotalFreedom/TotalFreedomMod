package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ProtectedArea;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_protectarea extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!TFM_Util.isUserSuperadmin(sender))
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (!TotalFreedomMod.protectedAreasEnabled)
        {
            sender.sendMessage("Protected areas are currently disabled in the TotalFreedomMod configuration.");
            return true;
        }

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("list"))
            {
                sender.sendMessage("Protected Areas: " + StringUtils.join(TFM_ProtectedArea.getProtectedAreaLabels(), ", "));
            }
            else if (args[0].equalsIgnoreCase("clear"))
            {
                TFM_ProtectedArea.clearProtectedAreas();
                
                sender.sendMessage("Protected Areas Cleared.");
            }
            else
            {
                return false;
            }

            return true;
        }
        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("remove"))
            {
                TFM_ProtectedArea.removeProtectedArea(args[1]);

                sender.sendMessage("Area removed. Protected Areas: " + StringUtils.join(TFM_ProtectedArea.getProtectedAreaLabels(), ", "));
            }
            else
            {
                return false;
            }

            return true;
        }
        else if (args.length == 3)
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                if (senderIsConsole)
                {
                    sender.sendMessage("You must be in-game to set a protected area.");
                    return true;
                }

                Double radius;
                try
                {
                    radius = Double.parseDouble(args[2]);
                }
                catch (NumberFormatException nfex)
                {
                    sender.sendMessage("Invalid radius.");
                    return true;
                }

                if (radius > TFM_ProtectedArea.MAX_RADIUS || radius < 0.0D)
                {
                    sender.sendMessage("Invalid radius. Radius must be a positive value less than " + TFM_ProtectedArea.MAX_RADIUS + ".");
                    return true;
                }

                TFM_ProtectedArea.addProtectedArea(args[1], sender_p.getLocation(), radius);

                sender.sendMessage("Area added. Protected Areas: " + StringUtils.join(TFM_ProtectedArea.getProtectedAreaLabels(), ", "));
            }
            else
            {
                return false;
            }

            return true;
        }

        return false;
    }
}
