package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ServiceChecker;
import me.StevenLawson.TotalFreedomMod.TFM_ServiceChecker.TFM_ServiceChecker_ServiceStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Shows the status of all Mojang services", usage = "/<command>")
public class Command_services extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerMsg("Mojang Services" + ChatColor.WHITE + ":", ChatColor.BLUE);

        for (TFM_ServiceChecker_ServiceStatus service : TFM_ServiceChecker.getInstance().getAllStatuses())
        {
            playerMsg(service.getFormattedStatus());
        }
        playerMsg("Version" + ChatColor.WHITE + ": " + TFM_ServiceChecker.getInstance().version, ChatColor.DARK_PURPLE);
        playerMsg("Last Check" + ChatColor.WHITE + ": " + TFM_ServiceChecker.getInstance().lastCheck, ChatColor.DARK_PURPLE);

        return true;
    }
}
