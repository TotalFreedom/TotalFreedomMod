package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.commandblocker.CommandBlocker;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Send a command as someone else.", usage = "/<command> <fromname> <outcommand>")
public class Command_gcmd extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 2)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        final String outCommand = StringUtils.join(args, " ", 1, args.length);

        if (plugin.cb.isCommandBlocked(outCommand, sender))
        {
            return true;
        }

        try
        {
            playerMsg("Sending command as " + player.getName() + ": " + outCommand);
            if (server.dispatchCommand(player, outCommand))
            {
                playerMsg("Command sent.");
            }
            else
            {
                playerMsg("Unknown error sending command.");
            }
        }
        catch (Throwable ex)
        {
            playerMsg("Error sending command: " + ex.getMessage());
        }

        return true;
    }
}
