package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//This command was coded initially by JeromSar

public class Command_uall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!(TFM_Util.isUserSuperadmin(sender) || senderIsConsole))
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        TFM_Util.adminAction(sender.getName(), "Undisguising all players", true);
        try
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                Bukkit.dispatchCommand(p, "u");
            }
        }
        catch (Throwable e)
        {
            sender.sendMessage(ChatColor.GRAY + "Error sending command: " + e.getMessage());
        }

        return true;
    }
}
