package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Toggle the disguise plugin", usage = "/<command>", aliases = "dtoggle")
public class Command_disguisetoggle extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (plugin.ldb.getLibsDisguisesPlugin() == null)
        {
            msg(ChatColor.RED + "LibsDisguises is not enabled.");
            return true;
        }

        boolean newState = !plugin.ldb.isPluginEnabled();
        FUtil.adminAction(sender.getName(), (newState ? "Enabling" : "Disabling") + " disguises", false);

        if (!newState)
        {
            plugin.ldb.undisguiseAll(true);
        }
        plugin.ldb.setPluginEnabled(newState);

        msg("Disguises are now " + (newState ? "enabled." : "disabled."));

        return true;
    }
}
