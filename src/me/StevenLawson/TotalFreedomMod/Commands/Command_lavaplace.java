package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Enable/disable lava placement.", usage = "/<command> <on | off>")
public class Command_lavaplace extends TFM_Command
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
            TotalFreedomMod.allowLavaPlace = true;
            playerMsg("Lava placement is now enabled.");
        }
        else
        {
            TotalFreedomMod.allowLavaPlace = false;
            playerMsg("Lava placement is now disabled.");
        }

        return true;
    }
}
