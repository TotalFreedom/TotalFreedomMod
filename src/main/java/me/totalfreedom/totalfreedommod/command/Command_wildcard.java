package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Run any command on all users, username placeholder = ?.", usage = "/<command> [fluff] ? [fluff] ?")
public class Command_wildcard extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("wildcard"))
        {
            msg("What the hell are you trying to do, you stupid idiot...", ChatColor.RED);
            return true;
        }
        if (args[0].equals("gtfo"))
        {
            msg("Nice try", ChatColor.RED);
            return true;
        }
        if (args[0].equals("doom"))
        {
            msg("Look, we all hate people, but this is not the way to deal with it, doom is evil enough!", ChatColor.RED);
            return true;
        }
        if (args[0].equals("saconfig"))
        {
            msg("WOA, WTF are you trying to do???", ChatColor.RED);
            return true;
        }

        String baseCommand = StringUtils.join(args, " ");

        if (plugin.cb.isCommandBlocked(baseCommand, sender))
        {
            // CommandBlocker handles messages and broadcasts
            return true;
        }

        for (Player player : server.getOnlinePlayers())
        {
            String out_command = baseCommand.replaceAll("\\x3f", player.getName());
            msg("Running Command: " + out_command);
            server.dispatchCommand(sender, out_command);
        }

        return true;
    }
}
