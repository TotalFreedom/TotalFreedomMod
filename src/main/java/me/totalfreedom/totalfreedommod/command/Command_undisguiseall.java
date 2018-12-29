package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.disguise.DisguiseBlocker;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
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
        if (!plugin.ldb.isEnabled())
        {
            msg("LibsDisguises is not enabled.");
            return true;
        }

        if (!DisguiseBlocker.enabled)
        {
            msg("Disguises are not enabled.");
            return true;
        }

        boolean admins = false;

        if (args.length > 0 && args[0].equalsIgnoreCase("-a"))
        {
            admins = true;
        }

        FUtil.adminAction(sender.getName(), "Undisguising all " + (admins ? "players" : "non-admins"), true);

        plugin.ldb.undisguiseAll(admins);

        return true;
    }
}
