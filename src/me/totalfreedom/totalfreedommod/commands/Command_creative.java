package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to creative, or define someone's username to change theirs.", usage = "/<command> [partialname]", aliases = "gmc")
public class Command_creative extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            if (args.length == 0)
            {
                sender.sendMessage("When used from the console, you must define a target user to change gamemode on.");
                return true;
            }
        }

        Player player;
        if (args.length == 0)
        {
            player = sender_p;
        }
        else
        {
            if (args[0].equalsIgnoreCase("-a"))
            {
                if (!isAdmin(sender))
                {
                    sender.sendMessage(FreedomCommand.MSG_NO_PERMS);
                    return true;
                }

                for (Player targetPlayer : server.getOnlinePlayers())
                {
                    targetPlayer.setGameMode(GameMode.CREATIVE);
                }

                FUtil.adminAction(sender.getName(), "Changing everyone's gamemode to creative", false);
                return true;
            }

            if (!(senderIsConsole || isAdmin(sender)))
            {
                playerMsg("Only superadmins can change other user's gamemode.");
                return true;
            }

            player = getPlayer(args[0]);

            if (player == null)
            {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }

        }

        playerMsg("Setting " + player.getName() + " to game mode 'Creative'.");
        playerMsg(player, sender.getName() + " set your game mode to 'Creative'.");
        player.setGameMode(GameMode.CREATIVE);

        return true;
    }
}
