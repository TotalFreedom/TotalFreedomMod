package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to adventure, or define someone's username to change theirs.", usage = "/<command> [partialname]")
public class Command_gma extends TFM_Command
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
                if (!TFM_SuperadminList.isUserSuperadmin(sender))
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

            if (!(senderIsConsole || TFM_SuperadminList.isUserSuperadmin(sender)))
            {
                playerMsg("Only superadmins can change other user's gamemode.");
                return true;
            }

            try
            {
                player = getPlayer(args[0]);
            }
            catch (PlayerNotFoundException ex)
            {
                sender.sendMessage(ex.getMessage());
                return true;
            }

        }

        playerMsg("Setting " + player.getName() + " to game mode 'Adventure'.");
        playerMsg(player, sender.getName() + " set your game mode to 'Adventure'.");
		playerMsg(player, sender.getName() + " please note that you won't be able to break most blocks'.");
        player.setGameMode(GameMode.ADVENTURE);

        return true;
    }
}
