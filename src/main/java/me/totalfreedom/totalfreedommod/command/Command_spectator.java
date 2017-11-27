

package me.totalfreedom.totalfreedommod.command;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import me.totalfreedom.totalfreedommod.rank.Rank;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to spectator.", usage = "/<command>", aliases = "gmsp")
public class Command_spectator extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length == 0) {
            if (this.isConsole()) {
                sender.sendMessage("When used from the console, you must define a target player.");
                return true;
            }
            playerSender.setGameMode(GameMode.SPECTATOR);
            this.msg("Gamemode set to spectator.");
            return true;
        }
        else {
            this.checkRank(Rank.SUPER_ADMIN);
            final Player player = this.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }
            this.msg("Setting " + player.getName() + " to game mode spectator");
            this.msg((CommandSender)player, sender.getName() + " set your game mode to spectator");
            player.setGameMode(GameMode.SPECTATOR);
            return true;
        }
    }
}
