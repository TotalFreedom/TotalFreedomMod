package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
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

        TotalFreedomMod.petProtectEnabled = !TFM_Util.isStopCommand(args[0]);

        TFM_Util.adminAction(
                sender.getName(),
                "Tamed pet protection is now " + (TotalFreedomMod.petProtectEnabled ? "enabled" : "disabled") + ".",
                false);

        return true;
    }
}
