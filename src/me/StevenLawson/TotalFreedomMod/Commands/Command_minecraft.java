package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ServiceChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Shows the uptime of all minecraft services.", usage = "/<command>")
public class Command_minecraft extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerMsg("Status of Mojang services:", ChatColor.BLUE);
        for (String service : TFM_ServiceChecker.getAllStatuses())
        {
            playerMsg(service);
        }
        playerMsg("Version " + TFM_ServiceChecker.version + ", Last Checked: " + TFM_ServiceChecker.last_updated, ChatColor.BLUE);
        return true;
    }
}
