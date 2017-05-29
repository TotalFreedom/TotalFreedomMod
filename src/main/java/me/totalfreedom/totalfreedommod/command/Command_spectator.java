package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to spectator, or define someone's username to change theirs.", usage = "/<command> <-a | [partialname]>", aliases = "gmsp")
public class Command_spectator extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length == 0) {
            if (this.isConsole()) {
                sender.sendMessage("When used from the console, you must define a target player.");
                return true;
            }
            this.checkRank(Rank.SUPER_ADMIN);
            playerSender.setGameMode(GameMode.SPECTATOR);
            this.msg("Gamemode set to Spectator.");
            return true;
        }
        else {
            this.checkRank(Rank.SUPER_ADMIN);
            if (args[0].equals("-a")) {
                for (final Player targetPlayer : this.server.getOnlinePlayers()) {
                    targetPlayer.setGameMode(GameMode.SPECTATOR);
                }
                FUtil.adminAction(sender.getName(), "Changing everyone's gamemode to spectator", false);
                return true;
            }
            final Player player = this.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }
            this.msg("Setting " + player.getName() + "'s game mode to spectator");
            this.msg((CommandSender)player, sender.getName() + " set your game mode to spectator");
            player.setGameMode(GameMode.SPECTATOR);
            return true;
        }
    }
}
