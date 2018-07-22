package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Set your compass to a position.", usage = "/<command> <x> <y> <z>")
public class Command_setcompass extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length < 3)
        {
            return false;
        }

        try
        {
            Location location = new Location(playerSender.getWorld(), Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]));
            playerSender.setCompassTarget(location);
            msg("Successfully set your compass coordinates to X: " + args[0] + ", Y: " + args[1] + ", Z: " + args[2] + ".", ChatColor.GREEN);
        }
        catch (NumberFormatException e)
        {
            msg("One or more of your coordinates are not a valid integer.", ChatColor.RED);
        }

        return true;
    }
}
