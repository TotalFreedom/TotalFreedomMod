package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Make arrows explode", usage = "/<command>", aliases = "ea")
public class Command_explosivearrows extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        boolean onList = plugin.it.explosivePlayers.contains(playerSender);
        if (onList)
        {
            plugin.it.explosivePlayers.remove(playerSender);
            msg("You no longer have explosive arrows", ChatColor.RED);
        }
        else
        {
            plugin.it.explosivePlayers.add(playerSender);
            msg("You now have explosive arrows", ChatColor.GREEN);
        }

        return true;
    }
}
