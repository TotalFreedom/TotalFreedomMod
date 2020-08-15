package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Undisguise all online players on the server", usage = "/<command> [-a]", aliases = "uall")
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

        boolean admins = false;

        if (args.length > 0 && args[0].equalsIgnoreCase("-a"))
        {
            admins = true;
        }

        FUtil.staffAction(sender.getName(), "Undisguising all " + (admins ? "players" : "non-staff"), true);

        plugin.ldb.undisguiseAll(admins);

        return true;
    }
}
