package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Manage banned VPN ips.", usage = "/<command> reload")
public class Command_vpnbanlist extends FreedomCommand
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

        msg("Reloading VPN ban list...", ChatColor.RED);
        plugin.vn.stop();
        plugin.vn.start();
        msg("Reloaded VPN ban list.");
        msg(plugin.vn.getVPNIps().size() + " IPs loaded");
        return true;
    }

}
