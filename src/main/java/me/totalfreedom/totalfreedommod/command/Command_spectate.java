package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Quickly spectate someone.", usage = "/<command> <playername>", aliases = "spec")
public class Command_spectate extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length == 0)
        {
            return false;
        }

        Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        if (player.getGameMode().equals(GameMode.SPECTATOR))
        {
            msg("You cannot spectate other players that are in spectator mode.", ChatColor.RED);
            return true;
        }

        if (!playerSender.getGameMode().equals(GameMode.SPECTATOR))
        {
            playerSender.setGameMode(GameMode.SPECTATOR);
        }

        playerSender.setSpectatorTarget(player);

        return true;
    }
}
