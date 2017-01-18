package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TELNET_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = /* the description is temporary! */ "Shows how many admins are connected to telnet", usage = "/<command>",
        aliases = /* /telnetlist is also temporary! It will be replaced when I get another thing
                to add to this command!*/ "tlnt,telnetlist")
public class Command_telnet extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        int amount = plugin.btb.getTelnetSessionAmount();
        msg("There " + (amount != 1 ? "are " + amount + " admins" : "is " + amount + " admin") + " connected to telnet.", ChatColor.GREEN);
        return true;
    }
}
