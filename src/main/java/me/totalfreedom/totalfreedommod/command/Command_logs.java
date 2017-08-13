package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.LogViewer.LogsRegistrationMode;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Register your connection with the TFM logviewer.", usage = "/<command> [off]")
public class Command_logs extends FreedomCommand
{

    @Override
    public boolean run(final CommandSender sender, final Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        LogsRegistrationMode mode = LogsRegistrationMode.ADD;
        if (args.length == 1 && "off".equalsIgnoreCase(args[0]))
        {
            mode = LogsRegistrationMode.DELETE;
        }
        plugin.lv.updateLogsRegistration(sender, playerSender, mode);

        return true;
    }
}
