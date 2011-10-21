package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_wildcard extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            if (args[0].equals("wildcard"))
            {
                sender.sendMessage("What the hell are you trying to do, you stupid idiot...");
                return true;
            }

            String base_command = TFM_Util.implodeStringList(" ", Arrays.asList(args));

            for (Player p : Bukkit.getOnlinePlayers())
            {
                String out_command = base_command.replaceAll("\\x3f", p.getName());
                sender.sendMessage("Running Command: " + out_command);
                Bukkit.getServer().dispatchCommand(sender, out_command);
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
