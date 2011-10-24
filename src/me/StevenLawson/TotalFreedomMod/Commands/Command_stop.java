package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_stop extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            TFM_Util.bcastMsg("Server is going offline.", ChatColor.GRAY);

            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.kickPlayer("Server is going offline, come back in a few minutes.");
            }

            Bukkit.shutdown();
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
