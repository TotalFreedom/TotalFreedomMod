package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Manage permanently banned players and IPs.", usage = "/<command> reload")
public class Command_permban extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (!args[0].equalsIgnoreCase("reload"))
        {
            return false;
        }

        playerMsg("Reloading permban list...", ChatColor.RED);
        plugin.pb.stop();
        plugin.pb.start();
        playerMsg("Reloaded permban list.");
        playerMsg(plugin.pb.getPermbannedIps().size() + " IPs and "
                + plugin.pb.getPermbannedNames().size() + " usernames loaded.");
        return true;
    }

}
