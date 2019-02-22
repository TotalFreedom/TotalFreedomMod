package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Goto the plot world", usage = "/<command>", aliases = "pw")
public class Command_plotworld extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        World plotworld = server.getWorld("plotworld");
        if (plotworld != null)
        {
            playerSender.teleport(plotworld.getSpawnLocation());
        }
        else
        {
            msg("\"plotworld\" doesn't exist.");
        }
        return true;
    }
}
