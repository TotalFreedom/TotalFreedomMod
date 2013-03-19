package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_RunSystemCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE, block_host_console = true, ignore_permissions = false)
public class Command_terminal extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        String command;
        try
        {
            StringBuilder command_bldr = new StringBuilder();
            for (int i = 0; i < args.length; i++)
            {
                command_bldr.append(args[i]).append(" ");
            }
            command = command_bldr.toString().trim();
        }
        catch (Throwable ex)
        {
            playerMsg("Error building command: " + ex.getMessage());
            return true;
        }

        playerMsg("Running system command: " + command);
        server.getScheduler().runTaskAsynchronously(plugin, new TFM_RunSystemCommand(command, plugin));

        return true;
    }
}
