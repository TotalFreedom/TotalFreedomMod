package me.totalfreedom.totalfreedommod.commands;

import lombok.Getter;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.pravian.aero.command.AbstractCommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public abstract class FreedomCommand extends AbstractCommandBase<TotalFreedomMod>
{

    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String NOT_FROM_CONSOLE = "This command may not be used from the console.";
    public static final String PLAYER_NOT_FOUND = ChatColor.GRAY + "Player not found!";
    //
    @Getter
    private final CommandParameters params;
    @Getter
    private final CommandPermissions perms;

    public FreedomCommand()
    {
        this.params = getClass().getAnnotation(CommandParameters.class);
        if (params == null)
        {
            FLog.warning("Ignoring command usage for command " + getClass().getSimpleName() + ". Command is not annotated!");
        }

        this.perms = getClass().getAnnotation(CommandPermissions.class);
        if (perms == null)
        {
            FLog.warning("Ignoring permissions for command " + getClass().getSimpleName() + ". Command is not annotated!");
        }
    }

    @Override
    public final boolean runCommand(final CommandSender sender, final Command command, final String label, final String[] args)
    {
        setVariables(sender, command, label, args);

        try
        {
            return run(sender, playerSender, command, label, args, isConsole());
        }
        catch (Exception ex)
        {
            FLog.severe("Uncaught exception executing command: " + command.getName());
            FLog.severe(ex);
            sender.sendMessage(ChatColor.RED + "Command error: " + (ex.getMessage() == null ? "Unknown cause" : ex.getMessage()));
            return true;
        }
    }

    protected abstract boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole);

    public void playerMsg(final CommandSender sender, final String message, final ChatColor color)
    {
        if (sender == null)
        {
            return;
        }
        sender.sendMessage(color + message);
    }

    public void playerMsg(final String message, final ChatColor color)
    {
        playerMsg(playerSender, message, color);
    }

    public void playerMsg(final CommandSender sender, final String message)
    {
        playerMsg(sender, message, ChatColor.GRAY);
    }

    public void playerMsg(final String message)
    {
        playerMsg(playerSender, message);
    }

    public boolean isAdmin(CommandSender sender)
    {
        return plugin.al.isAdmin(sender);
    }

    public Admin getAdmin(CommandSender sender)
    {
        return plugin.al.getAdmin(sender);
    }

    public Admin getAdmin(Player player)
    {
        return plugin.al.getAdmin(player);
    }

    public PlayerData getData(Player player)
    {
        return plugin.pl.getData(player);
    }

    public static FreedomCommand getCommand(Command command)
    {
        try
        {
            return (FreedomCommand) ((FreedomCommandExecutor) (((PluginCommand) command).getExecutor())).getCommandBase();
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
