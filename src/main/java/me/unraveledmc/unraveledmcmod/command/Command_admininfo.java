package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows how to apply for admin", usage = "/<command>", aliases = "ai")
public class Command_admininfo extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        msg("How to apply for admin on the UnraveledMC server:", ChatColor.AQUA);
        msg(" - Do not ask for admin in game,", ChatColor.DARK_GREEN);
        msg(" - Be helpful within the server,", ChatColor.GOLD);
        msg(" - Report those breaking the rules,", ChatColor.DARK_GREEN);
        msg(" - And apply on our forums at this link:", ChatColor.GOLD);
        msg(" - http://unraveledmc.com/forum", ChatColor.DARK_AQUA);
        msg(" - Do not apply for admin if you cannot be active!", ChatColor.RED);
        return true;
    }
}
