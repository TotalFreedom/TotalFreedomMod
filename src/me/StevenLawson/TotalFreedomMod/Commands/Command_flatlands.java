package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Goto the flatlands.", usage = "/<command>")
public class Command_flatlands extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (TotalFreedomMod.generateFlatlands)
        {
            TFM_Util.gotoWorld(sender, "flatlands");
        }
        else
        {
            playerMsg("Flatlands is currently disabled.");
        }
        return true;
    }
}
