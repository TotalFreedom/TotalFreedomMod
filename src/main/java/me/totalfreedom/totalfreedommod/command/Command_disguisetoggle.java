package me.totalfreedom.totalfreedommod.command;

import me.libraryaddict.disguise.DisallowedDisguises;
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
        if (!plugin.ldb.isPluginEnabled())
        {
            msg(ChatColor.RED + "LibsDisguises is not enabled.");
            return true;
        }

        FUtil.adminAction(sender.getName(), (DisallowedDisguises.disabled ? "Enabling" : "Disabling") + " Disguises", false);

        if (plugin.ldb.isDisguisesEnabled())
        {
            plugin.ldb.undisguiseAll(true);
            plugin.ldb.setDisguisesEnabled(false);
        }
        else
        {
            plugin.ldb.setDisguisesEnabled(true);
        }

        msg("Enabled " + (DisallowedDisguises.disabled ? "enabled." : "disabled."));

        return true;
    }
}
