package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Make other people your bitch.", usage = "/<command> <playername>")
public class Command_ride extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length < 1)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        if (player == playerSender)
        {
            msg("You can't ride yourself. smh.", ChatColor.RED);
            return true;
        }

        player.addPassenger(playerSender);

        return true;
    }
}
