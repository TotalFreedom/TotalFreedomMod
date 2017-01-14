package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.ArrayList;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Advertise a message every 10 minutes", usage = "/<command> <message>")
public class Command_advertise extends FreedomCommand
{
    public static ArrayList<Player> cooldown = new ArrayList<Player>();
    
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Player p = (Player) sender;
        
        if (cooldown.contains(p))
        {
            p.sendMessage(ChatColor.RED + "You must wait 10 minutes to make another advertisement.");
            return true;
        }
        
        if (args.length < 1)
        {
            return false;
        }
        
        String message = StringUtils.join(args, " ", 0, args.length);
        Bukkit.broadcastMessage(ChatColor.GREEN + "[Advertisement] " + ChatColor.GOLD + sender.getName() + ChatColor.GREEN + "] " + ChatColor.DARK_GREEN + message);
        
        // Begin cooldown
        cooldown.add(p);
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                // End cooldown
                p.sendMessage(ChatColor.GRAY + "You may now make another advertisement.");
                cooldown.remove(p);
            }
        }, 600L * 20L); 
    }
}
