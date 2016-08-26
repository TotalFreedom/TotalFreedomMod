package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to survival, or define someone's username to change theirs.", usage = "/<command> <[partialname] | -a>", aliases = "gms")
public class Command_survival extends FreedomCommand
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

            playerSender.setGameMode(GameMode.SURVIVAL);
            msg("Gamemode set to survival.");
            return true;
        }

        checkRank(Rank.SUPER_ADMIN);

        if (args[0].equals("-a"))
        {
            for (Player targetPlayer : server.getOnlinePlayers())
            {
                targetPlayer.setGameMode(GameMode.SURVIVAL);
            }

            FUtil.adminAction(sender.getName(), "Changing everyone's gamemode to survival", false);
            return true;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        msg("Setting " + player.getName() + " to game mode survival.");
        msg(player, sender.getName() + " set your game mode to survival.");
        player.setGameMode(GameMode.SURVIVAL);
        return true;
    }
}
