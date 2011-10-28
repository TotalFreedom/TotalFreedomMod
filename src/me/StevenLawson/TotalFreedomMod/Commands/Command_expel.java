package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Command_expel extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.NOT_FROM_CONSOLE);
        }
        else if (TFM_Util.isUserSuperadmin(sender, plugin))
        {
            double radius = 50.0;
            double strength = 100.0;

            if (args.length >= 1)
            {
                try
                {
                    radius = Math.max(1.0, Math.min(200.0, Double.parseDouble(args[0])));
                }
                catch (NumberFormatException nfex)
                {
                }
            }

            if (args.length >= 2)
            {
                try
                {
                    strength = Math.max(0.0, Math.min(200.0, Double.parseDouble(args[1])));
                }
                catch (NumberFormatException nfex)
                {
                }
            }

            Location sender_pos = sender_p.getLocation();
            for (Player p : sender_pos.getWorld().getPlayers())
            {
                if (!p.equals(sender_p))
                {
                    Location target_pos = p.getLocation();
                    
                    boolean in_range = false;
                    try
                    {
                        in_range = target_pos.distance(sender_pos) < radius;
                    }
                    catch (IllegalArgumentException ex)
                    {
                    }
                    
                    if (in_range)
                    {
                        sender.sendMessage("Pushing " + p.getName());
                        Vector expel_direction = target_pos.subtract(sender_pos).toVector().normalize();
                        p.setVelocity(expel_direction.multiply(strength));
                    }
                }
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
