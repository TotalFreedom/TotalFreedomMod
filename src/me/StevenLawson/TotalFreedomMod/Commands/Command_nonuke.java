package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE, block_host_console = true)
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
                TotalFreedomMod.nukeMonitorRange = Math.max(1.0, Math.min(500.0, Double.parseDouble(args[1])));
            }
            catch (NumberFormatException nfex)
            {
            }
        }

        if (args.length >= 3)
        {
            try
            {
                TotalFreedomMod.nukeMonitorCountBreak = Math.max(1, Math.min(500, Integer.parseInt(args[2])));
            }
            catch (NumberFormatException nfex)
            {
            }
        }

        if (args[0].equalsIgnoreCase("on"))
        {
            TotalFreedomMod.nukeMonitor = true;
            playerMsg("Nuke monitor is enabled.");
            playerMsg("Anti-freecam range is set to " + TotalFreedomMod.nukeMonitorRange + " blocks.");
            playerMsg("Block throttle rate is set to " + TotalFreedomMod.nukeMonitorCountBreak + " blocks destroyed per 5 seconds.");
        }
        else
        {
            TotalFreedomMod.nukeMonitor = false;
            playerMsg("Nuke monitor is disabled.");
        }

        return true;
    }
}
