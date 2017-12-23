package me.totalfreedom.totalfreedommod.command;

import java.io.File;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Removes essentials warps", usage = "/<command>")
public class Command_wipewarps extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!server.getPluginManager().isPluginEnabled("Essentials"))
        {
            msg("Essentials is not enabled on this server");
            return true;
        }

        Plugin essentials = server.getPluginManager().getPlugin("Essentials");
        FUtil.adminAction(sender.getName(), "Wiping Essentials Warps", true);
        server.getPluginManager().disablePlugin(essentials);
        FUtil.deleteFolder(new File(essentials.getDataFolder(), "warps"));
        server.getPluginManager().enablePlugin(essentials);
        msg("All warps deleted.");
        return true;
    }
}
