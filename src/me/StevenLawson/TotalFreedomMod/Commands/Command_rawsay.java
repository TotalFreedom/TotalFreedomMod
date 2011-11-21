package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_rawsay extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!senderIsConsole || sender.getName().equalsIgnoreCase("remotebukkit"))
        {
            sender.sendMessage(ChatColor.GRAY + "This command may only be used from the Telnet or BukkitHttpd console.");
            return true;
        }
        
        if (args.length == 0)
        {
            return false;
        }
        
        TFM_Util.bcastMsg(TFM_Util.implodeStringList(" ", Arrays.asList(args)));
        
        return true;
    }
}
