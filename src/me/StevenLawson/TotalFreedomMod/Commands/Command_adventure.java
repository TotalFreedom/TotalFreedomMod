package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to adventure, or define someone's username to change theirs.", usage = "/<command> <[partialname] | -a>")
public class Command_adventure extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            if (args.length == 0)
            {
                playerMsg("When used from the console, you must define a target user to change the gamemode to adventure on.");
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
                if (!TFM_SuperadminList.isUserSuperadmin(sender) || senderIsConsole)
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                for (Player targetPlayer : server.getOnlinePlayers())
                {
                    targetPlayer.setGameMode(GameMode.ADVENTURE);
                }

                TFM_Util.adminAction(sender.getName(), "Changing everyone's gamemode to adventure", false);
                return true;
            }

            if (senderIsConsole || TFM_SuperadminList.isUserSuperadmin(sender))
            {
                try
                {
                    player = getPlayer(args[0]);
                }
                catch (PlayerNotFoundException ex)
                {
                    playerMsg(ex.getMessage(), ChatColor.RED);
                    return true;
                }
            }
            else
            {
                playerMsg("Nice try :3 Only super admins can change peoples gamemode to adventure! *Mwhahahahahaha*.");
                return true;
            }
        }

        playerMsg("Setting " + player.getName() + " to game mode 'Adventure'.");
        player.sendMessage(sender.getName() + " set your game mode to 'Adventure'.");
		player.sendMessage(sender.getName() + " NOTE: You cannot break blocks whilst in adventure mode.");
        player.setGameMode(GameMode.ADVENTURE);

        return true;
    }
}
