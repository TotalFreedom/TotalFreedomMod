package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_explosives extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            if (args.length == 0)
            {
                return false;
            }

            if (args.length == 2)
            {
                try
                {
                    TotalFreedomMod.explosiveRadius = Math.max(1.0, Math.min(30.0, Double.parseDouble(args[1])));
                }
                catch (NumberFormatException nfex)
                {
                }
            }

            if (args[0].equalsIgnoreCase("on"))
            {
                TotalFreedomMod.allowExplosions = true;
                sender.sendMessage("Explosives are now enabled, radius set to " + TotalFreedomMod.explosiveRadius + " blocks.");
            }
            else
            {
                TotalFreedomMod.allowExplosions = false;
                sender.sendMessage("Explosives are now disabled.");
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
