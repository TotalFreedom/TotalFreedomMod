package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class TotalFreedomMod extends JavaPlugin
{
    private final TotalFreedomModEntityListener entityListener = new TotalFreedomModEntityListener(this);
    private final TotalFreedomModBlockListener blockListener = new TotalFreedomModBlockListener(this);
    //private final TotalFreedomModPlayerListener playerListener = new TotalFreedomModPlayerListener(this);
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    protected static Configuration CONFIG;
    private List<String> superadmins = new ArrayList<String>();
    public Boolean allowExplosions = false;
    public Boolean allowLavaDamage = false;
    public Boolean allowFire = false;
    
    public final static String MSG_NO_PERMS = ChatColor.YELLOW + "You do not have permission to use this command.";

    public void onEnable()
    {
        CONFIG = getConfiguration();
        CONFIG.load();
        if (CONFIG.getString("superadmins", null) == null) //Generate config file:
        {
            log.log(Level.INFO, "[Total Freedom Mod] - Generating default config file (plugins/TotalFreedomMod/config.yml)...");
            CONFIG.setProperty("superadmins", new String[] {"Madgeek1450", "markbyron"});
            CONFIG.setProperty("allow_explosions", false);
            CONFIG.setProperty("allow_lava_damage", false);
            CONFIG.setProperty("allow_fire", false);
            CONFIG.save();
            CONFIG.load();
        }
        superadmins = CONFIG.getStringList("superadmins", null);
        allowExplosions = CONFIG.getBoolean("allow_explosions", false);
        allowLavaDamage = CONFIG.getBoolean("allow_lava_damage", false);
        allowFire = CONFIG.getBoolean("allow_fire", false);
        
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Event.Priority.High, this);
        
        log.log(Level.INFO, "[Total Freedom Mod] - Enabled! - Version: " + this.getDescription().getVersion() + " by Madgeek1450");
        log.log(Level.INFO, "[Total Freedom Mod] - Loaded superadmins: " + implodeStringList(", ", superadmins));
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
            log.log(Level.INFO, String.format("[PLAYER_COMMAND] %s(%s): /%s %s", player.getName(), player.getDisplayName().replaceAll("\\xA7.", ""), commandLabel, implodeStringList(" ", Arrays.asList(args))));
        }
        else
        {
            log.log(Level.INFO, String.format("[CONSOLE_COMMAND] %s: /%s %s", sender.getName(), commandLabel, implodeStringList(" ", Arrays.asList(args))));
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
                }
                else
                {
                    sender.sendMessage(MSG_NO_PERMS);
                }
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("listreal") || cmd.getName().equalsIgnoreCase("list"))
        {
            StringBuilder onlineStats = new StringBuilder();
            StringBuilder onlineUsers = new StringBuilder();
            
            if (player == null)
            {
                onlineStats.append(String.format("There are %d out of a maximum %d players online.", Bukkit.getOnlinePlayers().length, Bukkit.getMaxPlayers()));
                
                onlineUsers.append("Connected players: ");
                boolean first = true;
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    if (first) first = false;
                    else onlineUsers.append(", ");
                    
                    if (sender.getName().equalsIgnoreCase("remotebukkit"))
                    {
                        onlineUsers.append(p.getName());
                    }
                    else
                    {
                        if (p.isOp()) onlineUsers.append("[OP]").append(p.getName());
                        else onlineUsers.append(p.getName());
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
                    if (first) first = false;
                    else onlineUsers.append(", ");
                    
                    if (p.isOp()) onlineUsers.append(ChatColor.RED).append(p.getName());
                    else onlineUsers.append(p.getName());
                    
                    onlineUsers.append(ChatColor.WHITE);
                }
            }
            
            sender.sendMessage(onlineStats.toString());
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

                tfBroadcastMessage(ChatColor.YELLOW + sender.getName() + " de-op'd everyone on the server.");
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("opall"))
        {
            if (isUserSuperadmin(sender.getName()) || player == null)
            {
                boolean doSetGamemode = false;
                GameMode targetGamemode = GameMode.CREATIVE;
                if (args.length != 0)
                {
                    if (args[0].equals("-c"))
                    {
                        doSetGamemode = true;
                        targetGamemode = GameMode.CREATIVE;
                    }
                    else if (args[0].equals("-s"))
                    {
                        doSetGamemode = true;
                        targetGamemode = GameMode.SURVIVAL;
                    }
                }
                
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    p.setOp(true);
                    if (doSetGamemode) p.setGameMode(targetGamemode);
                }
                
                tfBroadcastMessage(ChatColor.YELLOW + sender.getName() + " op'd everyone on the server.");
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
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
                    
                    tfBroadcastMessage("Oping " + p.getName());
                    p.sendMessage(ChatColor.YELLOW + "You are now op!");
                }
                if (!matched_player)
                {
                    sender.sendMessage("No targets matched.");
                }
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
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
                    
                    tfBroadcastMessage("De-opping " + p.getName());
                    p.sendMessage(ChatColor.YELLOW + "You have been de-op'd.");
                }
                if (!matched_player)
                {
                    sender.sendMessage("No targets matched.");
                }
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("survival"))
        {
            if (player == null)
            {
                if (args.length == 0)
                {
                    sender.sendMessage("When used from the console, you must define a target user to change gamemode on.");
                    return true;
                }
            }
            else
            {
                if (!sender.isOp())
                {
                    sender.sendMessage(MSG_NO_PERMS);
                    return true;
                }
            }
            
            Player p;
            if (args.length == 0)
            {
                p = Bukkit.getPlayerExact(sender.getName());
            }
            else
            {
                List<Player> matches = Bukkit.matchPlayer(args[0]);
                if (matches.isEmpty())
                {
                    sender.sendMessage("Can't find user " + args[0]);
                    return true;
                }
                else
                {
                    p = matches.get(0);
                }
            }
            
            sender.sendMessage("Setting " + p.getName() + " to game mode 'Survival'.");
            p.sendMessage(sender.getName() + " set your game mode to 'Survival'.");
            p.setGameMode(GameMode.SURVIVAL);
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("creative"))
        {
            if (player == null)
            {
                if (args.length == 0)
                {
                    sender.sendMessage("When used from the console, you must define a target user to change gamemode on.");
                    return true;
                }
            }
            else
            {
                if (!sender.isOp())
                {
                    sender.sendMessage(MSG_NO_PERMS);
                    return true;
                }
            }
            
            Player p;
            if (args.length == 0)
            {
                p = Bukkit.getPlayerExact(sender.getName());
            }
            else
            {
                List<Player> matches = Bukkit.matchPlayer(args[0]);
                if (matches.isEmpty())
                {
                    sender.sendMessage("Can't find user " + args[0]);
                    return true;
                }
                else
                {
                    p = matches.get(0);
                }
            }
            
            sender.sendMessage("Setting " + p.getName() + " to game mode 'Creative'.");
            p.sendMessage(sender.getName() + " set your game mode to 'Creative'.");
            p.setGameMode(GameMode.CREATIVE);
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("wildcard"))
        {
            if (player == null || isUserSuperadmin(sender.getName()))
            {
                if (args[0].equals("wildcard"))
                {
                    sender.sendMessage("What the hell are you trying to do, you stupid idiot...");
                    return true;
                }
                
                String base_command = implodeStringList(" ", Arrays.asList(args));

                for (Player p : Bukkit.getOnlinePlayers())
                {
                    String out_command = base_command.replaceAll("\\x3f", p.getName());
                    sender.sendMessage("Running Command: " + out_command);
                    Bukkit.getServer().dispatchCommand(sender, out_command);
                }
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("say"))
        {
            if (args.length == 0)
            {
                return false;
            }
            
            if (player == null || sender.isOp())
            {
                String message = implodeStringList(" ", Arrays.asList(args));
                tfBroadcastMessage(ChatColor.LIGHT_PURPLE + "[Server:" + sender.getName() + "] " + message);
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("gtfo"))
        {
            if (args.length != 1)
            {
                return false;
            }
            
            if (player == null || isUserSuperadmin(sender.getName()))
            {
                Player p;
                List<Player> matches = Bukkit.matchPlayer(args[0]);
                if (matches.isEmpty())
                {
                    sender.sendMessage("Can't find user " + args[0]);
                    return true;
                }
                else
                {
                    p = matches.get(0);
                }
                
                Bukkit.getServer().dispatchCommand(sender, "smite " + p.getName());
                
                p.setOp(false);
                
                String user_ip = p.getAddress().getAddress().toString().replaceAll("/", "").trim();
                
                tfBroadcastMessage(String.format("%sBanning: %s, IP: %s.", ChatColor.RED, p.getName(), user_ip));
                Bukkit.banIP(user_ip);
                Bukkit.getOfflinePlayer(p.getName()).setBanned(true);
                
                p.kickPlayer("GTFO");
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("stop"))
        {
            if (player == null || isUserSuperadmin(sender.getName()))
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    p.kickPlayer("Server is going offline, come back in a few minutes.");
                }
                
                Bukkit.shutdown();
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("explosives"))
        {
            if (player == null || isUserSuperadmin(sender.getName()))
            {
                if (args.length != 1)
                {
                    return false;
                }
                
                if (args[0].equalsIgnoreCase("on"))
                {
                    this.allowExplosions = true;
                    sender.sendMessage("Explosives are now enabled. Don't blow your fingers off!");
                }
                else
                {
                    this.allowExplosions = false;
                    sender.sendMessage("Explosives are now disabled. Funtime is over...");
                }
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("fire"))
        {
            if (player == null || isUserSuperadmin(sender.getName()))
            {
                if (args.length != 1)
                {
                    return false;
                }
                
                if (args[0].equalsIgnoreCase("on"))
                {
                    this.allowFire = true;
                    sender.sendMessage("Fire is now enabled.");
                }
                else
                {
                    this.allowFire = false;
                    sender.sendMessage("Fire is now disabled.");
                }
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
            }
            
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("lavadmg"))
        {
            if (player == null || isUserSuperadmin(sender.getName()))
            {
                if (args.length != 1)
                {
                    return false;
                }
                
                if (args[0].equalsIgnoreCase("on"))
                {
                    this.allowLavaDamage = true;
                    sender.sendMessage("Lava damage is now enabled.");
                }
                else
                {
                    this.allowLavaDamage = false;
                    sender.sendMessage("Lava damage is now disabled.");
                }
            }
            else
            {
                sender.sendMessage(MSG_NO_PERMS);
            }
            
            return true;
        }
        
        return false; 
    }
    
    public static void tfBroadcastMessage(String message)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(message);
        }
    }
    
    private static String implodeStringList(String glue, List<String> pieces)
    {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < pieces.size(); i++)
        {
            if (i != 0)
            {
                output.append(glue);
            }
            output.append(pieces.get(i));
        }
        return output.toString();
    }
    
    private boolean isUserSuperadmin(String userName)
    {
        return superadmins.contains(userName);
    }
}
