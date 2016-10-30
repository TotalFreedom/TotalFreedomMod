package me.totalfreedom.totalfreedommod.command;

import me.libraryaddict.disguise.DisallowedDisguises;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Undisguise all players on the server", usage = "/<command>", aliases = "uall")
public class Command_undisguiseall extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.ldb.isPluginEnabled())
        {
            msg("LibsDisguises is not enabled.");
            return true;
        }

        if (DisallowedDisguises.disabled)
        {
            msg("Disguises are not enabled.");
            return true;
        }

        FUtil.adminAction(sender.getName(), "Undisguising all non-admins", true);

        plugin.ldb.undisguiseAll(false);

        return true;
    }
}
