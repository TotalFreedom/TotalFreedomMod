package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to spectator, or define someone's username to change theirs.", usage = "/<command> <[partialname]>", aliases = "gmsp")
public class Command_spectator extends FreedomCommand
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

            playerSender.setGameMode(GameMode.SPECTATOR);
            msg("Gamemode set to spectator.");
            return true;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        msg("Setting " + player.getName() + " to game mode spectator");
        msg(player, sender.getName() + " set your game mode to spectator");
        player.setGameMode(GameMode.SPECTATOR);

        return true;
    }
}
