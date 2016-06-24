package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Quickly change your own gamemode to spectator.", usage = "/<command>", aliases = "gmsp")
public class Command_spectator extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerSender.setGameMode(GameMode.SPECTATOR);
        msg("Gamemode set to spectator.");
        return true;
    }
        checkRank(Rank.SUPER_ADMIN);

    if (args[0].equals("-a"))
    {
        for (Player targetPlayer : server.getOnlinePlayers())
        {
            targetPlayer.setGameMode(GameMode.SPECTATOR);
        }

        FUtil.adminAction(sender.getName(), "Changing everyone's gamemode to spectator", false);
        return true;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        msg("Setting " + player.getName() + " to game mode spectator.");
        msg(player, sender.getName() + " set your game mode to spectator.");
        player.setGameMode(GameMode.SPECTATOR);
        return true;
    }
}
