package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.ServiceChecker.ServiceStatus;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows the status of all Mojang services", usage = "/<command>")
public class Command_services extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        msg("Mojang Services" + ChatColor.WHITE + ":", ChatColor.BLUE);

        for (ServiceStatus service : plugin.sc.getAllStatuses())
        {
            msg(service.getFormattedStatus());
        }
        msg("Version" + ChatColor.WHITE + ": " + plugin.sc.getVersion(), ChatColor.GREEN);
        msg("Last Check" + ChatColor.WHITE + ": " + plugin.sc.getLastCheck(), ChatColor.GREEN);

        return true;
    }
}