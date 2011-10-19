package me.StevenLawson.TotalFreedomMod.Commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Command_saconfig extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!senderIsConsole || sender.getName().equalsIgnoreCase("remotebukkit"))
        {
            sender.sendMessage(ChatColor.GRAY + "This command may only be used from the Telnet or BukkitHttpd console.");
            return true;
        }
        
        if (args.length < 2)
        {
            return false;
        }
        
        if (args[0].equalsIgnoreCase("add"))
        {
            Player p;
            try
            {
                p = getPlayer(args[1]);
            }
            catch (CantFindPlayerException ex)
            {
                sender.sendMessage(ex.getMessage());
                return true;
            }
            
            String user_name = p.getName().toLowerCase().trim();
            String user_ip = p.getAddress().getAddress().toString().replaceAll("/", "").trim();
            
            sender.sendMessage(ChatColor.GRAY + "Adding " + user_name + " as a superadmin, with current IP = " + user_ip);
            
            if (!plugin.superadmins.contains(user_name))
            {
                plugin.superadmins.add(user_name);
            }
            
            if (!plugin.superadmin_ips.contains(user_ip))
            {
                plugin.superadmin_ips.add(user_ip);
            }
            
            try
            {
                FileConfiguration sa_config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
                sa_config.set("superadmins", plugin.superadmins);
                sa_config.set("superadmin_ips", plugin.superadmin_ips);
                sa_config.save(new File(plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
            }
            catch (IOException ex)
            {
                Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
            }
        }
        
        return true;
    }
}
