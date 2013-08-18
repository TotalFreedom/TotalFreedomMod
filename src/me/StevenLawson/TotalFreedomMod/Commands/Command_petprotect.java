package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Enable/disable tamed pet protection.", usage = "/<command> <on | off>")
public class Command_petprotect extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        TFM_Util.adminAction(
                sender.getName(),
                "Tamed pet protection is now " + (TFM_ConfigEntry.PET_PROTECT_ENABLED.setBoolean(!TFM_Util.isStopCommand(args[0])) ? "enabled" : "disabled") + ".",
                false);

        return true;
    }
}
