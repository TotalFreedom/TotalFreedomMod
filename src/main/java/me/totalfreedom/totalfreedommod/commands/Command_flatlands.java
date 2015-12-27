package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.NON_OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Goto the flatlands.", usage = "/<command>")
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
            playerMsg("Flatlands is currently disabled.");
        }
        return true;
    }
}
