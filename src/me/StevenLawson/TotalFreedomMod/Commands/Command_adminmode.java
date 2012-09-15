package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_adminmode extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!senderIsConsole || sender.getName().equalsIgnoreCase("remotebukkit"))
        {
            sender.sendMessage(ChatColor.GRAY + "This command may only be used from the Telnet or BukkitHttpd console.");
            return true;
        }

        if (args[0].equalsIgnoreCase("off"))
        {
            TotalFreedomMod.adminOnlyMode = false;
            TFM_Util.bcastMsg("Server is now open to all players.", ChatColor.RED);
            return true;
        }
        else if (args[0].equalsIgnoreCase("on"))
        {
            TotalFreedomMod.adminOnlyMode = true;
            TFM_Util.bcastMsg("Server is now closed to non-superadmins.", ChatColor.RED);
            for (Player p : server.getOnlinePlayers())
            {
                if (!TFM_Util.isUserSuperadmin(p))
                {
                    p.kickPlayer("Server is now closed to non-superadmins.");
                }
            }
            return true;
        }

        return false;
    }
}
