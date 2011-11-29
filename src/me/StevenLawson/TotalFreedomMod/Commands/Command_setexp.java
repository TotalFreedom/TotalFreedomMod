package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_setexp extends TFM_Command
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

        int exp_amount;

        try
        {
            exp_amount = Integer.parseInt(args[0]);

            if (exp_amount < 0)
            {
                exp_amount = 0;
            }
            else if (exp_amount > (int) Short.MAX_VALUE)
            {
                exp_amount = (int) Short.MAX_VALUE;
            }
        }
        catch (NumberFormatException ex)
        {
            sender.sendMessage(ChatColor.RED + "Invalid exp amount.");
            return true;
        }
        
        sender_p.setExperience(exp_amount);
        
        sender.sendMessage(ChatColor.AQUA + "Experience points set to: " + sender_p.getExperience());

        return true;
    }
}
