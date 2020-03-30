package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Go to the Flatlands.", usage = "/<command>")
public class Command_flatlands extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (ConfigEntry.FLATLANDS_GENERATE.getBoolean())
        {
            plugin.wm.flatlands.sendToWorld(playerSender);
        }
        else
        {
            msg("Flatlands is currently disabled in the TotalFreedomMod configuration.");
        }
        return true;
    }
}
