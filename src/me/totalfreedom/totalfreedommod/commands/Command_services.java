package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.ServiceChecker.ServiceStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows the status of all Mojang services", usage = "/<command>")
public class Command_services extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerMsg("Mojang Services" + ChatColor.WHITE + ":", ChatColor.BLUE);

        for (ServiceStatus service : plugin.sc.getAllStatuses())
        {
            playerMsg(service.getFormattedStatus());
        }
        playerMsg("Version" + ChatColor.WHITE + ": " + plugin.sc.getVersion(), ChatColor.DARK_PURPLE);
        playerMsg("Last Check" + ChatColor.WHITE + ": " + plugin.sc.getLastCheck(), ChatColor.DARK_PURPLE);

        return true;
    }
}
