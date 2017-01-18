package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.LogViewer.LogsRegistrationMode;
import me.unraveledmc.unraveledmcmod.rank.Rank;
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
        LogsRegistrationMode mode = LogsRegistrationMode.UPDATE;

        if (args.length == 1)
        {
            mode = ("off".equals(args[0]) ? LogsRegistrationMode.DELETE : LogsRegistrationMode.UPDATE);
        }

        plugin.lv.updateLogsRegistration(sender, playerSender, mode);

        return true;
    }

}
