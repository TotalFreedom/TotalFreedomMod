package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE, block_host_console = true)
@CommandParameters(description = "Attempt to detect \"invisible griefers\" and \"nukers\".", usage = "/<command> <on | off> [range] [blockrate]")
public class Command_nonuke extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        if (args.length >= 2)
        {
            try
            {
                TFM_ConfigEntry.NUKE_MONITOR_RANGE.setDouble(Math.max(1.0, Math.min(500.0, Double.parseDouble(args[1]))));
            }
            catch (NumberFormatException nfex)
            {
            }
        }

        if (args.length >= 3)
        {
            try
            {
                TFM_ConfigEntry.NUKE_MONITOR_COUNT_BREAK.setInteger(Math.max(1, Math.min(500, Integer.parseInt(args[2]))));
            }
            catch (NumberFormatException nfex)
            {
            }
        }

        if (args[0].equalsIgnoreCase("on"))
        {
            TFM_ConfigEntry.NUKE_MONITOR.setBoolean(true);
            playerMsg("Nuke monitor is enabled.");
            playerMsg("Anti-freecam range is set to " + TFM_ConfigEntry.NUKE_MONITOR_RANGE.getDouble() + " blocks.");
            playerMsg("Block throttle rate is set to " + TFM_ConfigEntry.NUKE_MONITOR_COUNT_BREAK.getInteger() + " blocks destroyed per 5 seconds.");
        }
        else
        {
            TFM_ConfigEntry.NUKE_MONITOR.setBoolean(false);
            playerMsg("Nuke monitor is disabled.");
        }

        return true;
    }
}
