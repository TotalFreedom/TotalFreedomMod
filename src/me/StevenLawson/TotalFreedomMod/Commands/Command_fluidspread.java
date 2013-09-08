package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Enable/disable fluid spread.", usage = "/<command> <on | off>")
public class Command_fluidspread extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        playerMsg("Lava and water spread is now " + (TFM_ConfigEntry.ALLOW_FLUID_SPREAD.setBoolean(!args[0].equalsIgnoreCase("off")) ? "enabled" : "disabled") + ".");

        return true;
    }
}
