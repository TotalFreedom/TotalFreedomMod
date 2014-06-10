package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to survival, or define someone's username to change theirs.", usage = "/<command> <[partialname] | -a>", aliases = "gms")
public class Command_survival extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
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
            player = sender_p;
        }
        else
        {
            if (args[0].equalsIgnoreCase("-a"))
            {
                if (!TFM_AdminList.isSuperAdmin(sender) || senderIsConsole)
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                for (Player targetPlayer : server.getOnlinePlayers())
                {
                    targetPlayer.setGameMode(GameMode.SURVIVAL);
                }

                TFM_Util.adminAction(sender.getName(), "Changing everyone's gamemode to survival", false);
                return true;
            }

            if (senderIsConsole || TFM_AdminList.isSuperAdmin(sender))
            {
                player = getPlayer(args[0]);

                if (player == null)
                {
                    playerMsg(TotalFreedomMod.PLAYER_NOT_FOUND);
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
