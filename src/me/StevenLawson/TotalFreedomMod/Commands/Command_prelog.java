package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.ADMIN_LEVEL;
import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.SOURCE_TYPE_ALLOWED;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SENIOR, source = SOURCE_TYPE_ALLOWED.ONLY_CONSOLE, block_host_console = true, ignore_permissions = false)
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
            TotalFreedomMod.preprocessLogEnabled = true;
            playerMsg("Command preprocess logging is now enabled. This will be spammy in the log.");
        }
        else
        {
            TotalFreedomMod.preprocessLogEnabled = false;
            playerMsg("Command preprocess logging is now disabled.");
        }

        return true;
    }
}
