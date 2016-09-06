package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Undisguise all players on the server", usage = "/<command> [-a]", aliases = "uall")
public class Command_undisguiseall extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Boolean admins = false;
        if (args.length > 0 && args[0].equals("-a"))
        {
            admins = true;
        }
        FUtil.adminAction(sender.getName(), "Undisguising all "  + (admins ? "players" : "non-admins"), true);
        plugin.ldb.undisguiseAll(admins);
        return true;
    }
}
