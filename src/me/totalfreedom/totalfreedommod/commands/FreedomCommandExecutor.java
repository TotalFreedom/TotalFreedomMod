package me.totalfreedom.totalfreedommod.commands;

import java.util.Arrays;
import me.totalfreedom.totalfreedommod.permission.ConsoleRank;
import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.permission.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.pravian.aero.command.AeroCommandBase;
import net.pravian.aero.command.executor.AbstractCommandExecutor;
import net.pravian.aero.command.executor.AeroCommandExecutor;
import net.pravian.aero.command.executor.AeroCommandExecutorFactory;
import net.pravian.aero.command.handler.AeroCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class FreedomCommandExecutor<C extends AeroCommandBase<?>> extends AbstractCommandExecutor<C>
{

    public FreedomCommandExecutor(AeroCommandHandler<?> handler, String name, C command)
    {
        super(handler, name, command);
    }

    protected FreedomCommand getCommand()
    {
        return commandBase instanceof FreedomCommand ? (FreedomCommand) commandBase : null;
    }

    @Override
    public void setupCommand(PluginCommand pluginCommand)
    {
        final FreedomCommand command = getCommand();
        if (command == null)
        {
            return;
        }

        final CommandParameters params = command.getParams();
        if (params == null)
        {
            return;
        }

        pluginCommand.setAliases(Arrays.asList(params.aliases().split(",")));
        pluginCommand.setDescription(params.description());
        pluginCommand.setUsage(params.usage());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!hasPermission(sender, true))
        {
            return true;
        }

        try
        {
            return commandBase.runCommand(sender, command, label, args);
        }
        catch (Exception ex)
        {
            // If this is ever ran, TFM failed :
            FLog.severe("Unhandled command exception: " + command.getName());
            FLog.severe(ex);
            sender.sendMessage(ChatColor.RED + "Unhandled Command Error: " + command.getName());
            return true;
        }
    }

    @Override
    public boolean hasPermission(CommandSender sender, boolean sendMsg)
    {
        final FreedomCommand command = getCommand();
        if (command == null)
        {
            return true;
        }

        final CommandPermissions perms = command.getPerms();
        if (perms == null)
        {
            return true;
        }

        // Block host console
        if (FUtil.isFromHostConsole(sender.getName())
                && perms.blockHostConsole())
        {
            if (sendMsg)
            {
                sender.sendMessage(handler.getPermissionMessage());
            }
            return false;
        }

        final Player player = sender instanceof Player ? (Player) sender : null;

        // Only console
        if (perms.source() == SourceType.ONLY_CONSOLE
                && player != null)
        {
            if (sendMsg)
            {
                sender.sendMessage(handler.getOnlyConsoleMessage());
            }
            return false;
        }

        // Only in game
        if (perms.source() == SourceType.ONLY_IN_GAME
                && player == null)
        {
            if (sendMsg)
            {
                sender.sendMessage(handler.getOnlyPlayerMessage());
            }
            return false;
        }

        // Player permissions
        if (player != null)
        {
            PlayerRank rank = TotalFreedomMod.plugin.rm.getRank(player);
            boolean result = rank.isAtLeast(perms.level());
            if (!result && sendMsg)
            {
                sender.sendMessage(handler.getPermissionMessage());
            }
            return result;
        }

        // Console permissions
        Rank rank = TotalFreedomMod.plugin.rm.getRank(sender);
        boolean result = rank.isAtLeast(ConsoleRank.forRank(perms.level()));
        if (!result && sendMsg)
        {
            sender.sendMessage(handler.getPermissionMessage());
        }

        return result;
    }

    public static class TFM_CommandExecutorFactory implements AeroCommandExecutorFactory
    {

        @Override
        public AeroCommandExecutor<? extends AeroCommandBase<?>> newExecutor(AeroCommandHandler<?> handler, String name, AeroCommandBase<?> command)
        {
            return new FreedomCommandExecutor<AeroCommandBase<?>>(handler, name, command);
        }

    }

}
