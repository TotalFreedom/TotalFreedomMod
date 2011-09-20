package me.StevenLawson.TotalFreedomMod;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TotalFreedomMod extends JavaPlugin
{
    private Logger log = Logger.getLogger("Minecraft");

    public void onEnable()
    {
        log.info("[Total Freedom Mod] - Enabled! - v1.0.0 by Madgeek1450");
    }

    public void onDisable()
    {
        log.info("[Total Freedom Mod] - Disabled.");
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
                if (isUserSuperadmin(sender))
                {
                    sender.setOp(true);
                    sender.sendMessage(ChatColor.YELLOW + "You are now op!");
                    log.log(Level.INFO, "[Total Freedom Mod]: {0} gave themselves op.", sender.getName());
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
                if (!first)
                {
                    onlineUsers.append(", ");
                    first = false;
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
            log.info("[Total Freedom Mod] - Got deopall command.");
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("opall"))
        {
            log.info("[Total Freedom Mod] - Got opall command.");
            return true;
        }
        return false; 
    }
    
    private boolean isUserSuperadmin(CommandSender sender)
    {
        return Arrays.asList(
                "miwojedk",
                "markbyron",
                "madgeek1450"
                ).contains(sender.getName().toLowerCase());
    }
}
