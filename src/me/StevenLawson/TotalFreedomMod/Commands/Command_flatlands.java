package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Flatlands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Goto the flatlands.", usage = "/<command>")
public class Command_flatlands extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (TFM_ConfigEntry.GENERATE_FLATLANDS.getBoolean())
        {
            TFM_Flatlands.getInstance().sendToWorld(sender_p);
        }
        else
        {
            playerMsg("Flatlands is currently disabled.");
        }
        return true;
    }
}
