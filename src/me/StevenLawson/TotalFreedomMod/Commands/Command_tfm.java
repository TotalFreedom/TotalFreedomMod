package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Shows information about TotalFreedomMod", usage = "/<command>")
public class Command_tfm extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerMsg("TotalFreedomMod for 'TotalFreedom', the original all-op server.", ChatColor.GOLD);
        playerMsg(String.format("Version " + ChatColor.BLUE + "%s.%s" + ChatColor.BLUE + ", built %s.", TotalFreedomMod.pluginVersion, TotalFreedomMod.buildNumber, TotalFreedomMod.buildDate), ChatColor.GOLD);
        playerMsg("Created by Madgeek1450 and DarthSalamon.", ChatColor.GOLD);
        playerMsg("Visit " + ChatColor.AQUA + "http://totalfreedom.me/" + ChatColor.GREEN + " for more information.", ChatColor.GREEN);

        return true;
    }
}
