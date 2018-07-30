package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Wipe the flatlands map. Requires manual restart after command is used.", usage = "/<command>")
public class Command_wipeflatlands extends FreedomCommand
{

    @Override
    public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        plugin.sf.setSavedFlag("do_wipe_flatlands", true);

        if (!ConfigEntry.FLATLANDS_GENERATE.getBoolean())
        {
            msg("Flatlands generation is disabled, therefore it cannot be wiped.");
            return true;
        }

        FUtil.bcastMsg("Server is going offline for flatlands wipe.", ChatColor.GRAY);

        if (plugin.wgb.isEnabled())
        {
            plugin.wgb.wipeRegions(plugin.wm.flatlands.getWorld());
        }

        for (Player player : server.getOnlinePlayers())
        {
            player.kickPlayer("Server is going offline for flatlands wipe, come back in a few minutes.");
        }

        if (!plugin.amp.enabled)
        {
            server.shutdown();
        }
        else
        {
            plugin.amp.restartServer();
        }

        return true;
    }
}