package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Go to a random location in the current world you are in", usage = "/<command>", aliases = "tpr,rtp")
public class Command_tprandom extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        int x = FUtil.randomInteger(-50000, 50000);
        int z = FUtil.randomInteger(-50000, 50000);
        int y = playerSender.getWorld().getHighestBlockYAt(x, z);
        Location location = new Location(playerSender.getLocation().getWorld(), x, y, z);
        playerSender.teleport(location);
        msg("Poof!", ChatColor.GREEN);
        return true;
    }
}
