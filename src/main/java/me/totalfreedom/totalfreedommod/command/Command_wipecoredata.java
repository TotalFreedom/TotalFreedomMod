package me.totalfreedom.totalfreedommod.command;

import java.io.File;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

@CommandPermissions(level = Rank.TELNET_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Wipe the CoreProtect data", usage = "/<command> [purge / flatlands]")
public class Command_wipecoredata extends FreedomCommand
{

    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!server.getPluginManager().isPluginEnabled("CoreProtect"))
        {
            msg("CoreProtect is not enabled on this server");
            return true;
        }

        if (args.length == 1)
        {
            switch (args[0])
            {
                case "purge":
                    FUtil.adminAction(sender.getName(), "Purging CoreProtect data", true);
                    final PluginManager pm = server.getPluginManager();
                    final Plugin target = pm.getPlugin("CoreProtect");
                    pm.disablePlugin(target);
                    File coreFile = new File(server.getPluginManager().getPlugin("CoreProtect").getDataFolder() + File.separator + "database.db");
                    FUtil.deleteFile(coreFile);
                    pm.enablePlugin(target);
                    msg("All CoreProtect data deleted.");
                    return true;
                case "flatlands":
                    FUtil.adminAction(sender.getName(), "Wiping CoreProtect data in flatlands", true);
                    plugin.cpd.deleteFlatlandsDatabase();
                    msg("All CoreProtect data in flatlands deleted.");
                    return true;
                default:
                    msg("Invalid argument!");
                    return false;
            }
        }

        FUtil.adminAction(sender.getName(), "Wiping CoreProtect data", true);
        plugin.cpd.deleteOtherData();
        msg("All non-world specific CoreProtect data deleted.");
        return true;
    }
}
