package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Toggle the disguise plugin", usage = "/<command>", aliases = "dtoggle")
public class Command_disguisetoggle extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        
        final PluginManager pm = server.getPluginManager();
        
        Plugin LibsDisguises = null;
        
        if (server.getPluginManager().getPlugin("LibsDisguises") != null)
        {
            LibsDisguises = pm.getPlugin("LibsDisguises");
        }
        else
        {
            msg("LibsDisguises has not been found.");
            return true;
        }
        
        Boolean enabled = pm.isPluginEnabled(LibsDisguises);
        
        FUtil.adminAction(sender.getName(), (!enabled ? "Enabling" : "Disabling") + " LibsDisguises", true);
        
        if (enabled)
        {
            plugin.ldb.undisguiseAll();
            pm.disablePlugin(LibsDisguises);
            enabled = false;
        }
        else
        {
            pm.enablePlugin(LibsDisguises);
            enabled = true;
        }

        return true;
    }
}
