package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class TFM_Command
{
    protected TotalFreedomMod plugin;
    protected Server server;
    private CommandSender commandSender;
    private Class<?> commandClass;

    public TFM_Command()
    {
    }

    abstract public boolean run(final CommandSender sender, final Player sender_p, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole);

    public void setup(final TotalFreedomMod plugin, final CommandSender commandSender, final Class<?> commandClass)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.commandSender = commandSender;
        this.commandClass = commandClass;
    }

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
        playerMsg(commandSender, message, color);
    }

    public void playerMsg(final CommandSender sender, final String message)
    {
        playerMsg(sender, message, ChatColor.GRAY);
    }

    public void playerMsg(final String message)
    {
        playerMsg(commandSender, message);
    }

    public boolean senderHasPermission()
    {
        final CommandPermissions permissions = commandClass.getAnnotation(CommandPermissions.class);

        if (permissions == null)
        {
            TFM_Log.warning(commandClass.getName() + " is missing permissions annotation.");
            return true;
        }

        boolean isSuper = TFM_AdminList.isSuperAdmin(commandSender);
        boolean isSenior = false;

        if (isSuper)
        {
            isSenior = TFM_AdminList.isSeniorAdmin(commandSender);
        }

        final AdminLevel level = permissions.level();
        final SourceType source = permissions.source();
        final boolean blockHostConsole = permissions.blockHostConsole();

        if (!(commandSender instanceof Player))
        {
            if (source == SourceType.ONLY_IN_GAME)
            {
                return false;
            }

            if (level == AdminLevel.SENIOR && !isSenior)
            {
                return false;
            }

            if (blockHostConsole && TFM_Util.isFromHostConsole(commandSender.getName()))
            {
                return false;
            }

            return true;
        }

        final Player senderPlayer = (Player) commandSender;

        if (source == SourceType.ONLY_CONSOLE)
        {
            return false;
        }

        if (level == AdminLevel.SENIOR)
        {
            if (!isSenior)
            {
                return false;
            }

            if (!TFM_PlayerData.getPlayerData(senderPlayer).isSuperadminIdVerified())
            {
                return false;
            }

            return true;
        }

        if (level == AdminLevel.SUPER && !isSuper)
        {
            return false;
        }

        if (level == AdminLevel.OP && !senderPlayer.isOp())
        {
            return false;
        }

        return true;
    }

    public Player getPlayer(final String partialname)
    {
        List<Player> matches = server.matchPlayer(partialname);
        if (matches.isEmpty())
        {
            for (Player player : server.getOnlinePlayers())
            {
                if (player.getDisplayName().toLowerCase().contains(partialname.toLowerCase()))
                {
                    return player;
                }
            }
            return null;
        }
        else
        {
            return matches.get(0);
        }
    }
}
