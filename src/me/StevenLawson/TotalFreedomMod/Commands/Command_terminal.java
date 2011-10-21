package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_RunSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_terminal extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!senderIsConsole || sender.getName().equalsIgnoreCase("remotebukkit"))
        {
            sender.sendMessage(ChatColor.GRAY + "This command may only be used from the Telnet or BukkitHttpd console.");
            return true;
        }

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
            sender.sendMessage(ChatColor.GRAY + "Error building command: " + ex.getMessage());
            return true;
        }

        sender.sendMessage("Running system command: " + command);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new TFM_RunSystemCommand(command, plugin));

        return true;
    }
}
