package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Vanilla command", usage = "/<command> <entity> <x> <y> <z> <command> OR /<command> <entity> <x> <y> <z> detect <x> <y> <z> <block> <data> <command>")
public class Command_execute extends FreedomCommand
{

    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        FLog.warning("[Anti-execute] Command detector: " + sender.getName() + " Just tried to run Execute command with args: " + StringUtils.join(args, " "));
        return true;
    }

}
