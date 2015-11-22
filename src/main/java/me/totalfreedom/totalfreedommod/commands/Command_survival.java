package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to survival, or define someone's username to change theirs.", usage = "/<command> <[partialname] | -a>", aliases = "gms")
public class Command_survival extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            if (args.length == 0)
            {
                playerMsg("When used from the console, you must define a target user to change gamemode on.");
                return true;
            }
        }

        Player player;

        if (args.length == 0)
        {
            player = playerSender;
        }
        else
        {
            if (args[0].equalsIgnoreCase("-a"))
            {
                if (!plugin.al.isAdmin(sender) || senderIsConsole)
                {
                    noPerms();
                    return true;
                }

                for (Player targetPlayer : server.getOnlinePlayers())
                {
                    targetPlayer.setGameMode(GameMode.SURVIVAL);
                }

                FUtil.adminAction(sender.getName(), "Changing everyone's gamemode to survival", false);
                return true;
            }

            if (senderIsConsole || plugin.al.isAdmin(sender))
            {
                player = getPlayer(args[0]);

                if (player == null)
                {
                    playerMsg(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }
            }
            else
            {
                playerMsg("Only superadmins can change other user's gamemode.");
                return true;
            }
        }

        playerMsg("Setting " + player.getName() + " to game mode 'Survival'.");
        player.sendMessage(sender.getName() + " set your game mode to 'Survival'.");
        player.setGameMode(GameMode.SURVIVAL);

        return true;
    }
}
