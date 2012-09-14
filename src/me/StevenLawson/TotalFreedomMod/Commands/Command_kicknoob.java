package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_kicknoob extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!senderIsConsole || sender.getName().equalsIgnoreCase("remotebukkit"))
        {
            sender.sendMessage(ChatColor.GRAY + "This command may only be used from the Telnet or BukkitHttpd console.");
            return true;
        }
        
        TFM_Util.adminAction(sender.getName(), "Disconnecting all non-superadmins.", true);
        
        for (Player p : server.getOnlinePlayers())
        {
            if (!TFM_Util.isUserSuperadmin(p))
            {
                p.kickPlayer("Disconnected by admin.");
            }
        }
        
        return true;
    }
}
