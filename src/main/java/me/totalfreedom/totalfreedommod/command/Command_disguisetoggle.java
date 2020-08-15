package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Toggle LibsDisguises for everyone online.", usage = "/<command>", aliases = "dtoggle")
public class Command_disguisetoggle extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.ldb.isEnabled())
        {
            msg("LibsDisguises is not enabled.");
            return true;
        }

        FUtil.staffAction(sender.getName(), (plugin.ldb.isDisguisesEnabled() ? "Disabling" : "Enabling") + " disguises", false);

        if (plugin.ldb.isDisguisesEnabled())
        {
            plugin.ldb.undisguiseAll(true);
            plugin.ldb.setDisguisesEnabled(false);
        }
        else
        {
            plugin.ldb.setDisguisesEnabled(true);
        }

        msg("Disguises are now " + (plugin.ldb.isDisguisesEnabled() ? "enabled." : "disabled."));

        return true;
    }
}
