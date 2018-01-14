package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Go to a random place in the current world you are in", usage = "/<command>", aliases = "tpr")
public class Command_tprandom extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        int x = FUtil.random(-10000, 10000);
        int z = FUtil.random(-10000, 10000);
        int y = playerSender.getWorld().getHighestBlockYAt(x, z);
        Location location = new Location(playerSender.getLocation().getWorld(), x, y, z);
        playerSender.teleport(location);
        msg("Poof!", ChatColor.GREEN);
        return true;
    }
}