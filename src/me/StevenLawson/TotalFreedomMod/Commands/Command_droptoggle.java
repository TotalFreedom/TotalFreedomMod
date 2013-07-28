package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Enable/disable dropping.", usage = "/<command> <on | off>")
public class Command_firespread extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }
        
        if (args[0].equalsIgnoreCase("on"))
        {
            TotalFreedomMod.autoEntityWipe = true;
            playerMsg("Drops are now enabled!");
            TFM_Util.adminAction(sender.getName(), "Enabling drops", false);
        }
        else
        {
            TotalFreedomMod.autoEntityWipe = false;
            playerMsg("Drops are now disabled.");
            TFM_Util.adminAction(sender.getName(), "Disabling drops", true);
        }
        
        return true;
    }
}
