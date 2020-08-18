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

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "Denies joining of operators and only allows staff members to join.", usage = "/<command> [on | off]")
public class Command_staffmode extends FreedomCommand
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
            ConfigEntry.STAFF_ONLY_MODE.setBoolean(false);
            FUtil.staffAction(sender.getName(), "Opening the server to all players.", true);
            return true;
        }
        else if (args[0].equalsIgnoreCase("on"))
        {
            ConfigEntry.STAFF_ONLY_MODE.setBoolean(true);
            FUtil.staffAction(sender.getName(), "Closing the server to non-staff.", true);
            for (Player player : server.getOnlinePlayers())
            {
                if (!isStaff(player))
                {
                    player.kickPlayer("Server is now closed to non-staff.");
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1 && plugin.sl.isStaff(sender) && !(sender instanceof Player))
        {
            return Arrays.asList("on", "off");
        }

        return Collections.emptyList();
    }
}
