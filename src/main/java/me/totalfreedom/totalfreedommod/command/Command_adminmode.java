package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Close server to non-admins.", usage = "/<command> [on | off]")
public class Command_adminmode extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("off"))
        {
            ConfigEntry.ADMIN_ONLY_MODE.setBoolean(false);
            FUtil.adminAction(sender.getName(), "Opening the server to all players.", true);
            return true;
        }
        else if (args[0].equalsIgnoreCase("on"))
        {
            ConfigEntry.ADMIN_ONLY_MODE.setBoolean(true);
            FUtil.adminAction(sender.getName(), "Closing the server to non-admins.", true);
            for (Player player : server.getOnlinePlayers())
            {
                if (!isAdmin(player))
                {
                    player.kickPlayer("Server is now closed to non-admins.");
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1 && plugin.al.isAdmin(sender) && !(sender instanceof Player))
        {
            return Arrays.asList("on", "off");
        }

        return Collections.emptyList();
    }
}
