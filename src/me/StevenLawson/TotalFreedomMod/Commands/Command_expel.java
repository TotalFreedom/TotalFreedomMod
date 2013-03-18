package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME, ignore_permissions = false)
public class Command_expel extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        double radius = 15.0;
        double strength = 20.0;

        if (args.length >= 1)
        {
            try
            {
                radius = Math.max(1.0, Math.min(100.0, Double.parseDouble(args[0])));
            }
            catch (NumberFormatException nfex)
            {
            }
        }

        if (args.length >= 2)
        {
            try
            {
                strength = Math.max(0.0, Math.min(50.0, Double.parseDouble(args[1])));
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
                    in_range = target_pos.distanceSquared(sender_pos) < (radius * radius);
                }
                catch (IllegalArgumentException ex)
                {
                }

                if (in_range)
                {
                    p.setVelocity(target_pos.clone().subtract(sender_pos).toVector().normalize().multiply(strength));
                    sender.sendMessage("Pushing " + p.getName() + ".");
                }
            }
        }

        return true;
    }
}
