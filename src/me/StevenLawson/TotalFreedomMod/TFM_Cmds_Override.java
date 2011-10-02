package me.StevenLawson.TotalFreedomMod;

import java.util.Arrays;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//TFM_Cmds_Override:
//say
//stop
//list/listreal

public class TFM_Cmds_Override implements CommandExecutor
{
    private TotalFreedomMod plugin;
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    public TFM_Cmds_Override(TotalFreedomMod plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        try
        {
            Player sender_p = null;
            boolean senderIsConsole = false;
            if (sender instanceof Player)
            {
                sender_p = (Player) sender;
                log.info(String.format("[PLAYER_COMMAND] %s(%s): /%s %s", sender_p.getName(), ChatColor.stripColor(sender_p.getDisplayName()), commandLabel, plugin.implodeStringList(" ", Arrays.asList(args))));
            }
            else
            {
                senderIsConsole = true;
                log.info(String.format("[CONSOLE_COMMAND] %s: /%s %s", sender.getName(), commandLabel, plugin.implodeStringList(" ", Arrays.asList(args))));
            }
            
            if (cmd.getName().equalsIgnoreCase("say"))
            {
                if (args.length == 0)
                {
                    return false;
                }

                if (senderIsConsole || sender.isOp())
                {
                    String message = plugin.implodeStringList(" ", Arrays.asList(args));
                    plugin.tfm_broadcastMessage(String.format("[Server:%s] %s", sender.getName(), message), ChatColor.LIGHT_PURPLE);
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("stop"))
            {
                if (senderIsConsole || plugin.isUserSuperadmin(sender))
                {
                    plugin.tfm_broadcastMessage("Server is going offline.", ChatColor.GRAY);

                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        p.kickPlayer("Server is going offline, come back in a few minutes.");
                    }

                    Bukkit.shutdown();
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }

                return true;
            }
            else if (cmd.getName().equalsIgnoreCase("listreal") || cmd.getName().equalsIgnoreCase("list"))
            {
                StringBuilder onlineStats = new StringBuilder();
                StringBuilder onlineUsers = new StringBuilder();

                if (senderIsConsole)
                {
                    onlineStats.append(String.format("There are %d out of a maximum %d players online.", Bukkit.getOnlinePlayers().length, Bukkit.getMaxPlayers()));

                    onlineUsers.append("Connected players: ");
                    boolean first = true;
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (first)
                        {
                            first = false;
                        }
                        else
                        {
                            onlineUsers.append(", ");
                        }

                        if (sender.getName().equalsIgnoreCase("remotebukkit"))
                        {
                            onlineUsers.append(p.getName());
                        }
                        else
                        {
                            if (p.isOp())
                            {
                                onlineUsers.append("[OP]").append(p.getName());
                            }
                            else
                            {
                                onlineUsers.append(p.getName());
                            }
                        }
                    }
                }
                else
                {
                    onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(Bukkit.getOnlinePlayers().length);
                    onlineStats.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(Bukkit.getMaxPlayers());
                    onlineStats.append(ChatColor.BLUE).append(" players online.");

                    onlineUsers.append("Connected players: ");
                    boolean first = true;
                    for (Player p : Bukkit.getOnlinePlayers())
                    {
                        if (first)
                        {
                            first = false;
                        }
                        else
                        {
                            onlineUsers.append(", ");
                        }

                        if (p.isOp())
                        {
                            onlineUsers.append(ChatColor.RED).append(p.getName());
                        }
                        else
                        {
                            onlineUsers.append(p.getName());
                        }

                        onlineUsers.append(ChatColor.WHITE);
                    }
                }

                sender.sendMessage(onlineStats.toString());
                sender.sendMessage(onlineUsers.toString());

                return true;
            }
        }
        catch (Exception ex)
        {
            log.severe("Exception in TFM_Cmds_Override.onCommand(): " + ex.getMessage());
        }
        
        return false;
    }
}
