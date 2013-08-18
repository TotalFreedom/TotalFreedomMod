package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Enable/disable fire placement.", usage = "/<command> <on | off>")
public class Command_fireplace extends TFM_Command
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
            TFM_ConfigEntry.ALLOW_FIRE_PLACE.setBoolean(true);
            playerMsg("Fire placement is now enabled.");
        }
        else
        {
            TFM_ConfigEntry.ALLOW_FIRE_PLACE.setBoolean(false);
            playerMsg("Fire placement is now disabled.");
        }

        return true;
    }
}
