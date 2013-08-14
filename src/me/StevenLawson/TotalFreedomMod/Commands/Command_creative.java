package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to creative, or define someone's username to change theirs.", usage = "/<command> [partialname]")
public class Command_creative extends TFM_Command
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

        Player target;
        if (args.length == 0)
        {
            target = sender_p;
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

                for (Player player : server.getOnlinePlayers())
                {
                    player.setGameMode(GameMode.CREATIVE);
                }

                TFM_Util.adminAction(sender.getName(), "Changing everyone's gamemode to creative", false);
                return true;
            }

            if (!(senderIsConsole || TFM_SuperadminList.isUserSuperadmin(sender)))
            {
                playerMsg("Only superadmins can change other user's gamemode.");
                return true;
            }

            try
            {
                target = getPlayer(args[0]);
            }
            catch (PlayerNotFoundException e)
            {
                sender.sendMessage(e.getMessage());
                return true;
            }

        }

        playerMsg("Setting " + target.getName() + " to game mode 'Creative'.");
        playerMsg(target, sender.getName() + " set your game mode to 'Creative'.");
        target.setGameMode(GameMode.CREATIVE);

        return true;
    }
}
