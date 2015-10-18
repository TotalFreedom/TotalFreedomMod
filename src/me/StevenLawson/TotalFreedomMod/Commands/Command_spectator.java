package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Change yours or another players gamemode to Spectator Mode.", usage = "/<command> [partialname] | [-a]", aliases = "spec")
public class Command_spectator extends TFM_Command
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            if (args.length == 0)
            {
                sender.sendMessage("When sending this command through console you must specify a player.");
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
                if (!TFM_AdminList.isSuperAdmin(sender))
                {
                    sender.sendMessage(TFM_Command.MSG_NO_PERMS);
                    return true;
                }

                for (Player targetPlayer : server.getOnlinePlayers())
                {
                    targetPlayer.setGameMode(GameMode.CREATIVE);
                }

                TFM_Util.adminAction(sender.getName(), "Changing everyone's gamemode to spectator mode.", false);
                return true;
            }
            
            if (!(senderIsConsole || TFM_AdminList.isSuperAdmin(sender)))
            {
                playerMsg("Only superadmins can change other user's gamemode.");
                return true;
            }
            
            player = getPlayer(args[0]);

            if (player == null)
            {
                sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
                return true;
            }

        }

        playerMsg("Setting " + player.getName() + " to game mode 'Spectator Mode'.");
        playerMsg(player, sender.getName() + " set your game mode to 'Spectator Mode'.");
        player.setGameMode(GameMode.SPECTATOR);
        
        return true;
        }
    }
