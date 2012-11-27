package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SENIOR, source = SOURCE_TYPE_ALLOWED.ONLY_CONSOLE, block_web_console = true, ignore_permissions = false)
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
            sender.sendMessage("Command preprocess logging is now enabled. This will be spammy in the log.");
        }
        else
        {
            TotalFreedomMod.preprocessLogEnabled = false;
            sender.sendMessage("Command preprocess logging is now disabled.");
        }

        return true;
    }
}
