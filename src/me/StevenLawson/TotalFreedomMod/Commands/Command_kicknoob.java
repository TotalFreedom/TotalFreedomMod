package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Kick all non-superadmins on server.", usage = "/<command>")
public class Command_kicknoob extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Util.adminAction(sender.getName(), "Disconnecting all non-superadmins.", true);

        for (Player player : server.getOnlinePlayers())
        {
            if (!TFM_AdminList.isSuperAdmin(player))
            {
                player.kickPlayer(ChatColor.RED + "Disconnected by admin.");
            }
        }

        return true;
    }
}
