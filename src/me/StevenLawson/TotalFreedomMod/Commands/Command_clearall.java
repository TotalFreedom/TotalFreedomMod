package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH)
@CommandParameters(description = "Clears all supered ips of all admins.", usage = "/<command>")
public class Command_clearall extends TFM_Command
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        TFM_Util.adminAction(sender.getName(), "Cleaning all supered IPs of all admins.", true);

        for (Player player : Bukkit.getOnlinePlayers())
        {
            final TFM_Admin admin = TFM_AdminList.getEntry(player);
            final String ip = TFM_Util.getIp(player);
            admin.clearIPs();
            admin.addIp(ip);
            TFM_AdminList.saveAll();
            playerMsg(admin.getIps().get(0) + " is now your only IP address");

        }
        return true;
    }
}
