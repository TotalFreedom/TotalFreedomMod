package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH, block_host_console = true)
@CommandParameters(description = "Kick all non-superadmins on server.", usage = "/<command> (annon)")
public class Command_kicknoob extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Util.adminAction(sender.getName(), sender_p + "Disconnecting all non-superadmins.", true);

        for (Player p : server.getOnlinePlayers())
        {
            if (!TFM_SuperadminList.isUserSuperadmin(p))
            {
                p.kickPlayer(ChatColor.RED + "Disconnected by" + sender_p);
            }
        }
        String mode = args[0].toLowerCase();
        if (mode.equalsIgnoreCase("annon"))
        for (Player p : server.getOnlinePlayers())
        {
            if (!TFM_SuperadminList.isUserSuperadmin(p))
            {
                p.kickPlayer(ChatColor.RED + "Disconnected by a admin");
            }
        }
    

        return true;
    }
}
