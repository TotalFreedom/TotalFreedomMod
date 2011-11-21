package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

public class Command_addexp extends TFM_Command
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
                sender.sendMessage(ChatColor.AQUA + "Invalid exp amount (MUST BE POSITIVE).");
                return true;
            }
            else if (exp_amount > 2000)
            {
                sender.sendMessage(ChatColor.AQUA + "Invalid exp amount (MAX = 2000).");
                return true;
            }
        }
        catch (NumberFormatException ex)
        {
            sender.sendMessage(ChatColor.AQUA + "Invalid exp amount.");
            return true;
        }
        
        ExperienceOrb exp_orb = sender_p.getWorld().spawn(sender_p.getLocation(), ExperienceOrb.class);
        exp_orb.setExperience(exp_amount);
        
        sender.sendMessage(ChatColor.AQUA + String.valueOf(exp_amount) + " exp added.");
        
        return true;
    }
}
