package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to adventure, or define someone's username to change theirs.", usage = "/<command> <-a | [partialname]>", aliases = "gma")
public class Command_adventure extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            if (isConsole())
            {
                sender.sendMessage("When used from the console, you must define a target player.");
                return true;
            }

            playerSender.setGameMode(GameMode.ADVENTURE);
            msg("Gamemode set to adventure.");
            return true;
        }

        if (args[0].equals("-a"))
        {
            for (Player targetPlayer : server.getOnlinePlayers())
            {
                targetPlayer.setGameMode(GameMode.ADVENTURE);
            }

            FUtil.adminAction(sender.getName(), "Changing everyone's gamemode to adventure", false);
            return true;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        msg("Setting " + player.getName() + " to game mode adventure");
        msg(player, sender.getName() + " set your game mode to adventure");
        player.setGameMode(GameMode.ADVENTURE);

        return true;
    }
}
