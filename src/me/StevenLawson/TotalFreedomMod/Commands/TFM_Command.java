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

public class TFM_Command
{
    protected TotalFreedomMod plugin;
    protected Server server;
    private CommandSender commandsender;

    public TFM_Command()
    {
    }

    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Log.severe("Command Error: Command not implemented: " + cmd.getName());
        sender.sendMessage(ChatColor.RED + "Command Error: Command not implemented: " + cmd.getName());
        return false;
    }

    public void setPlugin(TotalFreedomMod plugin)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    public void setCommandsender(CommandSender commandsender)
    {
        this.commandsender = commandsender;
    }

    public void playerMsg(CommandSender sender, String message, ChatColor color) // complete function
    {
        sender.sendMessage(color + message);
    }

    public void playerMsg(String message, ChatColor color)
    {
        playerMsg(commandsender, message, color);
    }

    public void playerMsg(CommandSender sender, String message)
    {
        playerMsg(sender, message, ChatColor.GRAY);
    }

    public void playerMsg(String message)
    {
        playerMsg(commandsender, message);
    }

    public boolean senderHasPermission(Class<?> cmd_class, CommandSender sender)
    {
        CommandPermissions permissions = cmd_class.getAnnotation(CommandPermissions.class);
        if (permissions != null)
        {
            if (permissions.ignore_permissions())
            {
                return true;
            }
            else
            {
                boolean is_super = TFM_SuperadminList.isUserSuperadmin(sender);
                boolean is_senior = false;
                if (is_super)
                {
                    is_senior = TFM_SuperadminList.isSeniorAdmin(sender);
                }

                AdminLevel level = permissions.level();
                SourceType source = permissions.source();
                boolean block_host_console = permissions.block_host_console();

                Player sender_p = null;
                if (sender instanceof Player)
                {
                    sender_p = (Player) sender;
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
                    else if (block_host_console && TFM_Util.isFromHostConsole(sender.getName()))
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
        }
        return true;
    }

    public Player getPlayer(String partialname) throws CantFindPlayerException
    {
        List<Player> matches = server.matchPlayer(partialname);
        if (matches.isEmpty())
        {
            for (Player p : server.getOnlinePlayers())
            {
                if (p.getDisplayName().toLowerCase().contains(partialname.toLowerCase()))
                {
                    return p;
                }
            }
            throw new CantFindPlayerException(partialname);
        }
        else
        {
            return matches.get(0);
        }
    }
}
