package me.StevenLawson.TotalFreedomMod.Commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
        
//        if (args[0].equalsIgnoreCase("add"))
//        {
//            Player p;
//            try
//            {
//                p = getPlayer(args[1]);
//            }
//            catch (CantFindPlayerException ex)
//            {
//                sender.sendMessage(ex.getMessage());
//                return true;
//            }
//            
//            String user_name = p.getName().toLowerCase().trim();
//            String user_ip = p.getAddress().getAddress().toString().replaceAll("/", "").trim();
//            
//            sender.sendMessage(ChatColor.GRAY + "Adding " + user_name + " as a superadmin, with current IP = " + user_ip);
//            
//            if (!plugin.superadmins.contains(user_name))
//            {
//                plugin.superadmins.add(user_name);
//            }
//            
//            if (!plugin.superadmin_ips.contains(user_ip))
//            {
//                plugin.superadmin_ips.add(user_ip);
//            }
//            
//            try
//            {
//                FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
//                config.set("superadmins", plugin.superadmins);
//                config.set("superadmin_ips", plugin.superadmin_ips);
//                config.save(new File(plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
//            }
//            catch (IOException ex)
//            {
//                Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
//            }
//        }
        
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
            String new_ip = p.getAddress().getAddress().toString().replaceAll("/", "").trim();
            
            boolean something_changed = false;
            
            if (!plugin.superadmins.contains(user_name))
            {
                plugin.superadmins.add(user_name);
                sender.sendMessage("Adding new superadmin: " + user_name);
                something_changed = true;
            }
            
            if (!plugin.superadmin_ips.contains(new_ip))
            {
                plugin.superadmin_ips.add(new_ip);
                sender.sendMessage("Adding new superadmin IP: " + new_ip);
                something_changed = true;
            }
            
            if (!something_changed)
            {
                sender.sendMessage("That superadmin/superadmin ip pair already exists. Nothing to change!");
            }
            
            FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
            
            List<String> user_ips = new ArrayList<String>();
            if (config.contains(user_name))
            {
                user_ips = config.getStringList(user_name);
            }
            
            if (!user_ips.contains(new_ip))
            {
                user_ips.add(new_ip);
            }
            
            config.set(user_name, user_ips);
            
            try
            {
                config.save(new File(plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
            }
            catch (IOException ex)
            {
                log.log(Level.SEVERE, null, ex);
            }
        }
        
        return true;
    }
}
