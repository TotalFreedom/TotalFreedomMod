package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Manage permanently banned players and IPs.", usage = "/<command> reload")
public class Command_permban extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (!args[0].equalsIgnoreCase("reload"))
        {
            return false;
        }

        msg("Reloading permban list...", ChatColor.RED);
        plugin.pm.stop();
        plugin.pm.start();
        msg("Reloaded permban list.");
        msg(plugin.pm.getPermbannedIps().size() + " IPs and "
                + plugin.pm.getPermbannedNames().size() + " usernames loaded.");
        return true;
    }

}
