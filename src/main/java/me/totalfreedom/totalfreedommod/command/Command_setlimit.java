package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Sets everyone's Worldedit block modification limit to the default limit or to a custom limit.", usage = "/<command> [limit]", aliases = "setl,swl")
public class Command_setlimit extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        int amount = 2500;
        if (args.length > 0)
        {
            try
            {
                amount = Math.max(1, Math.min(10000, Integer.parseInt(args[0])));
            }
            catch (NumberFormatException ex)
            {
                msg("Invalid number: " + args[0], ChatColor.RED);
                return true;
            }
        }
        FUtil.adminAction(sender.getName(), "Setting everyone's Worldedit block modification limit to " + amount + ".", true);
        for (final Player player : server.getOnlinePlayers())
        {
            plugin.web.setLimit(player, amount);
        }
        return true;
    }
}
