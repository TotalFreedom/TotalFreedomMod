package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_setlevel extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.NOT_FROM_CONSOLE);
            return true;
        }

        if (!sender.isOp())
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (args.length != 1)
        {
            return false;
        }
        
        int new_level;

        try
        {
            new_level = Integer.parseInt(args[0]);
            
            if (new_level < 0)
            {
                new_level = 0;
            }
            else if (new_level > 50)
            {
                new_level = 50;
            }
        }
        catch (NumberFormatException ex)
        {
            sender.sendMessage(ChatColor.AQUA + "Invalid level.");
            return true;
        }

        sender_p.setLevel(new_level);
        
        sender.sendMessage(ChatColor.AQUA + "You have been set to level " + Integer.toString(new_level));
        
        return true;
    }
}
