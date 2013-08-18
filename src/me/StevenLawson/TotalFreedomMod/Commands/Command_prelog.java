package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE, block_host_console = true)
@CommandParameters(
        description = "Enable/disable the command prelogger. When this is on, logs will be filled with many duplicate messages.",
        usage = "/<command> <on | off>")
public class Command_prelog extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("on"))
        {
            TFM_ConfigEntry.PREPROCESS_LOG_ENABLED.setBoolean(true);
            playerMsg("Command preprocess logging is now enabled. This will be spammy in the log.");
        }
        else
        {
            TFM_ConfigEntry.PREPROCESS_LOG_ENABLED.setBoolean(false);
            playerMsg("Command preprocess logging is now disabled.");
        }

        return true;
    }
}
