package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_firespread extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            if (args.length != 1)
            {
                return false;
            }

            if (args[0].equalsIgnoreCase("on"))
            {
                TotalFreedomMod.allowFireSpread = true;
                sender.sendMessage("Fire spread is now enabled.");
            }
            else
            {
                TotalFreedomMod.allowFireSpread = false;
                sender.sendMessage("Fire spread is now disabled.");
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
