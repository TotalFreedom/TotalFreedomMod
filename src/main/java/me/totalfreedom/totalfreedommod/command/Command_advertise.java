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
        
        if (cooldown.contains(playerSender))
        {
            msg("You must wait 10 minutes to make another advertisement.");
            return true;
        }
        
        if (args.length == 0)
        {
            return false;
        }
        
        String message = StringUtils.join(args, " ", 0, args.length);
        Bukkit.broadcastMessage(ChatColor.GREEN + "[Advertisement] " + ChatColor.GOLD + sender.getName() + ChatColor.GREEN + "> " + ChatColor.DARK_GREEN + message);
        
        // Begin cooldown
        cooldown.add(playerSender);
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                // End cooldown
                msg("You may now make another advertisement.");
                cooldown.remove(playerSender);
            }
        }, 600L * 20L); 
    }
}
