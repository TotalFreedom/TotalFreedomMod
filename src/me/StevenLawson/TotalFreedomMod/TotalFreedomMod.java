package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class TotalFreedomMod extends JavaPlugin
{
    private static final Logger log = Logger.getLogger("Minecraft");
    protected static Configuration CONFIG;
    private List<String> superadmins = new ArrayList<String>();

    public void onEnable()
    {
        CONFIG = getConfiguration();
        CONFIG.load();
        if (CONFIG.getString("superadmins", null) == null)
        {
            log.log(Level.INFO, "[Total Freedom Mod] - Generating default config file (plugins/TotalFreedomMod/config.yml)...");
            CONFIG.setProperty("superadmins", new String[] {"Madgeek1450", "markbyron"});
            CONFIG.save();
            CONFIG.load();
        }
        superadmins = CONFIG.getStringList("superadmins", null);
        
        log.log(Level.INFO, "[Total Freedom Mod] - Enabled! - Version: " + this.getDescription().getVersion() + " by Madgeek1450");
        log.log(Level.INFO, "[Total Freedom Mod] - Loaded superadmins: " + superadmins.toString());
    }

    public void onDisable()
    {
        log.log(Level.INFO, "[Total Freedom Mod] - Disabled.");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = null;
        if (sender instanceof Player)
        {
            player = (Player)sender;
        }
        
        if(cmd.getName().equalsIgnoreCase("opme"))
        {
            if (player == null)
            {
                sender.sendMessage("This command only works in-game.");
            }
            else
            {
                if (isUserSuperadmin(sender.getName()))
                {
                    sender.setOp(true);
                    sender.sendMessage(ChatColor.YELLOW + "You are now op!");
                    log.log(Level.INFO, "[Total Freedom Mod]: " + sender.getName() + " gave themselves op.");
                }
                else
                {
                    sender.sendMessage(ChatColor.YELLOW + "You do not have permission to use this command.");
                }
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("listreal"))
        {
            StringBuilder online = new StringBuilder();
            online.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(Bukkit.getOnlinePlayers().length);
            online.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(Bukkit.getMaxPlayers());
            online.append(ChatColor.BLUE).append(" players online.");
            sender.sendMessage(online.toString());

            StringBuilder onlineUsers = new StringBuilder();
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
            sender.sendMessage(onlineUsers.toString());

            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("deopall"))
        {
            if (isUserSuperadmin(sender.getName()) || player == null)
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    if (!isUserSuperadmin(p.getName()) && !p.getName().equals(sender.getName()))
                    {
                        p.setOp(false);
                    }
                }
                
                log.log(Level.INFO, "[Total Freedom Mod]: " + sender.getName() + " used deopall.");
                Bukkit.broadcastMessage(ChatColor.YELLOW + sender.getName() + " de-op'd everyone on the server.");
            }
            else
            {
                sender.sendMessage(ChatColor.YELLOW + "You do not have permission to use this command.");
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("opall"))
        {
            if (isUserSuperadmin(sender.getName()) || player == null)
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    p.setOp(true);
                }
                
                log.log(Level.INFO, "[Total Freedom Mod]: " + sender.getName() + " used opall.");
                Bukkit.broadcastMessage(ChatColor.YELLOW + sender.getName() + " op'd everyone on the server.");
            }
            else
            {
                sender.sendMessage(ChatColor.YELLOW + "You do not have permission to use this command.");
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("qop")) //Quick OP
        {
            if (args.length != 1)
            {
                return false;
            }
        
            if (sender.isOp() || player == null || isUserSuperadmin(sender.getName()))
            {
                boolean matched_player = false;
                for (Player p : Bukkit.matchPlayer(args[0]))
                {
                    matched_player = true;
                    
                    p.setOp(true);
                    
                    Command.broadcastCommandMessage(sender, "Oping " + p.getName());
                    p.sendMessage(ChatColor.YELLOW + "You are now op!");
                    log.log(Level.INFO, "[Total Freedom Mod]: " + sender.getName() + " op'd " + p.getName() + ".");
                }
                if (!matched_player)
                {
                    sender.sendMessage("No targets matched.");
                }
            }
            else
            {
                sender.sendMessage(ChatColor.YELLOW + "You do not have permission to use this command.");
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("qdeop")) //Quick De-op
        {
            if (args.length != 1)
            {
                return false;
            }
        
            if (sender.isOp() || player == null || isUserSuperadmin(sender.getName()))
            {
                boolean matched_player = false;
                for (Player p : Bukkit.matchPlayer(args[0]))
                {
                    matched_player = true;
                    
                    p.setOp(false);
                    
                    Command.broadcastCommandMessage(sender, "De-opping " + p.getName());
                    p.sendMessage(ChatColor.YELLOW + "You are now op!");
                    log.log(Level.INFO, "[Total Freedom Mod]: " + sender.getName() + " de-op'd " + p.getName() + ".");
                }
                if (!matched_player)
                {
                    sender.sendMessage("No targets matched.");
                }
            }
            else
            {
                sender.sendMessage(ChatColor.YELLOW + "You do not have permission to use this command.");
            }
            
            return true;
        }
        return false; 
    }
    
    private boolean isUserSuperadmin(String userName)
    {
        return superadmins.contains(userName);
    }
}
