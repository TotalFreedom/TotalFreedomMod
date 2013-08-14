package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
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
        this.server = this.plugin.getServer();
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
        CommandPermissions permissions = commandClass.getAnnotation(CommandPermissions.class);
        if (permissions != null)
        {
            boolean is_super = TFM_SuperadminList.isUserSuperadmin(this.commandSender);
            boolean is_senior = false;
            if (is_super)
            {
                is_senior = TFM_SuperadminList.isSeniorAdmin(this.commandSender);
            }

            AdminLevel level = permissions.level();
            SourceType source = permissions.source();
            boolean block_host_console = permissions.block_host_console();

            Player sender_p = null;
            if (this.commandSender instanceof Player)
            {
                sender_p = (Player) this.commandSender;
            }

            if (sender_p == null)
            {
                if (source == SourceType.ONLY_IN_GAME)
                {
                    return false;
                }
                else if (level == AdminLevel.SENIOR && !is_senior)
                {
                    return false;
                }
                else if (block_host_console && TFM_Util.isFromHostConsole(this.commandSender.getName()))
                {
                    return false;
                }
            }
            else
            {
                if (source == SourceType.ONLY_CONSOLE)
                {
                    return false;
                }
                else if (level == AdminLevel.SENIOR)
                {
                    if (is_senior)
                    {
                        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(sender_p);
                        Boolean superadminIdVerified = playerdata.isSuperadminIdVerified();

                        if (superadminIdVerified != null)
                        {
                            if (!superadminIdVerified.booleanValue())
                            {
                                return false;
                            }
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
                else if (level == AdminLevel.SUPER && !is_super)
                {
                    return false;
                }
                else if (level == AdminLevel.OP && !sender_p.isOp())
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            TFM_Log.warning(commandClass.getName() + " is missing permissions annotation.");
        }
        return true;
    }

    public Player getPlayer(final String partialname) throws PlayerNotFoundException
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
            throw new PlayerNotFoundException(partialname);
        }
        else
        {
            return matches.get(0);
        }
    }
}
