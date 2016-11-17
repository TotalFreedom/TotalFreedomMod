package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Send a command as someone else.", usage = "/<command> <fromname> <outcommand>")
public class Command_gcmd extends FreedomCommand
{

    public static final List<String> BLOCKED_COMMANDS = (List<String>) ConfigEntry.BLOCKED_GCMD_COMMANDS.getList();

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        try
        {
            final Player player = getPlayer(args[0]);

            if (args.length < 2)
            {
                return false;
            }

            if (player == null)
            {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }

            String outCommand = StringUtils.join(args, " ", 1, args.length);
            Command runCmd = server.getPluginCommand(args[1]);

            if (BLOCKED_COMMANDS.contains(runCmd.getName()))
            {
                msg("Blocked. Yes, I mean blocked.", ChatColor.RED);
                return true;
            }

            if (plugin.cb.isCommandBlocked(outCommand, sender))
            {
                return true;
            }

            try
            {
                msg("Sending command as " + player.getName() + ": " + outCommand);
                if (server.dispatchCommand(player, outCommand))
                {
                    msg("Command sent.");
                    player.sendMessage(sender.getName() + " just ran the command: [/" + outCommand + "]" + " as you!");
                    FLog.info("Alert! User: " + sender.getName() + " just ran the command: [/" + outCommand + "]" + " as " + player.getName());
                }
                else
                {
                    msg("Unknown error sending command.");
                }
            }
            catch (Throwable ex)
            {
                msg("Error sending command: " + ex.getMessage());
            }
        }
        catch (Exception e)
        {
            msg("/gcmd <fromname> <outcommand>");
        }
        return true;

    }
}
