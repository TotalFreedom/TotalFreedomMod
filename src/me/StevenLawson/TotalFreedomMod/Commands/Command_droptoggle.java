package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Enable/disable auto entity wiper.", usage = "/<command> <on | off>")
public class Command_droptoggle extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }
        TFM_Util.adminAction(sender.getName(),
                (TFM_ConfigEntry.AUTO_ENTITY_WIPE.setBoolean(!args[0].equalsIgnoreCase("off")) ? "Enabled" : "Disabled")
                + " automatic entity wiping.", false);
        return true;
    }
}
