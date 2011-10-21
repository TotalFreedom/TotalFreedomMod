package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_Command;
import me.StevenLawson.TotalFreedomMod.Listener.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TotalFreedomMod extends JavaPlugin
{
    private static final Logger log = Logger.getLogger("Minecraft");
    
    public static final long HEARTBEAT_RATE = 5L; //Seconds
    public static final String CONFIG_FILE = "config.yml";
    public static final String SUPERADMIN_FILE = "superadmin.yml";
    private static final String COMMAND_PATH = "me.StevenLawson.TotalFreedomMod.Commands";
    private static final String COMMAND_PREFIX = "Command_";
    
    public static final String MSG_NO_PERMS = ChatColor.YELLOW + "You do not have permission to use this command.";
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String CAKE_LYRICS = "But there's no sense crying over every mistake. You just keep on trying till you run out of cake.";
    public static final String NOT_FROM_CONSOLE = "This command may not be used from the console.";
    
    public Map<Player, TFM_UserInfo> userinfo = new HashMap<Player, TFM_UserInfo>();
    public List<TFM_LandmineData> landmines = new ArrayList<TFM_LandmineData>();
    public boolean allPlayersFrozen = false;

    @Override
    public void onEnable()
    {
        loadMainConfig();
        loadSuperadminConfig();
        
        registerEventHandlers();
        
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new TFM_Heartbeat(this), HEARTBEAT_RATE * 20L, HEARTBEAT_RATE * 20L);

        log.log(Level.INFO, "[" + getDescription().getName() + "] - Enabled! - Version: " + getDescription().getVersion() + " by Madgeek1450");
        
        TFM_Util.deleteFolder(new File("./_deleteme"));
    }

    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
        log.log(Level.INFO, "[" + getDescription().getName() + "] - Disabled.");
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
                log.info(String.format("[PLAYER_COMMAND] %s(%s): /%s %s",
                        sender_p.getName(),
                        ChatColor.stripColor(sender_p.getDisplayName()),
                        commandLabel,
                        TFM_Util.implodeStringList(" ", Arrays.asList(args))));
            }
            else
            {
                senderIsConsole = true;
                log.info(String.format("[CONSOLE_COMMAND] %s: /%s %s",
                        sender.getName(),
                        commandLabel,
                        TFM_Util.implodeStringList(" ", Arrays.asList(args))));
            }

            TFM_Command dispatcher;
            try
            {
                ClassLoader classLoader = TotalFreedomMod.class.getClassLoader();
                dispatcher = (TFM_Command) classLoader.loadClass(String.format("%s.%s%s", COMMAND_PATH, COMMAND_PREFIX, cmd.getName().toLowerCase())).newInstance();
                dispatcher.setPlugin(this);
            }
            catch (Throwable ex)
            {
                log.log(Level.SEVERE, "[" + getDescription().getName() + "] Command not loaded: " + cmd.getName(), ex);
                sender.sendMessage(ChatColor.RED + "Command Error: Command not loaded: " + cmd.getName());
                return true;
            }

            try
            {
                return dispatcher.run(sender, sender_p, cmd, commandLabel, args, senderIsConsole);
            }
            catch (Throwable ex)
            {
                sender.sendMessage(ChatColor.RED + "Command Error: " + ex.getMessage());
            }
        }
        catch (Throwable ex)
        {
            log.log(Level.SEVERE, "[" + getDescription().getName() + "] Command Error: " + commandLabel, ex);
            sender.sendMessage(ChatColor.RED + "Unknown Command Error.");
        }

        return true;
    }
    
    public boolean allowFirePlace = false;
    public Boolean allowFireSpread = false;
    public Boolean allowLavaDamage = false;
    public boolean allowLavaPlace = false;
    public boolean allowWaterPlace = false;
    public Boolean allowExplosions = false;
    public double explosiveRadius = 4.0D;
    public boolean autoEntityWipe = true;
    public boolean nukeMonitor = true;
    public int nukeMonitorCountBreak = 100;
    public int nukeMonitorCountPlace = 25;
    public double nukeMonitorRange = 10.0D;
    public int freecamTriggerCount = 10;
    public Boolean preprocessLogEnabled = true;
    public Boolean disableNight = true;
    public Boolean disableWeather = true;

    public void loadMainConfig()
    {
        TFM_Util.createDefaultConfiguration(CONFIG_FILE, this, getFile());
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), CONFIG_FILE));

        allowFirePlace = config.getBoolean("allow_fire_place", allowFirePlace);
        allowFireSpread = config.getBoolean("allow_fire_spread", allowFireSpread);
        allowLavaDamage = config.getBoolean("allow_lava_damage", allowLavaDamage);
        allowLavaPlace = config.getBoolean("allow_lava_place", allowLavaPlace);
        allowWaterPlace = config.getBoolean("allow_water_place", allowWaterPlace);
        allowExplosions = config.getBoolean("allow_explosions", allowExplosions);
        explosiveRadius = config.getDouble("explosiveRadius", explosiveRadius);
        autoEntityWipe = config.getBoolean("auto_wipe", autoEntityWipe);
        nukeMonitor = config.getBoolean("nuke_monitor", nukeMonitor);
        nukeMonitorCountBreak = config.getInt("nuke_monitor_count_break", nukeMonitorCountBreak);
        nukeMonitorCountPlace = config.getInt("nuke_monitor_count_place", nukeMonitorCountPlace);
        nukeMonitorRange = config.getDouble("nuke_monitor_range", nukeMonitorRange);
        freecamTriggerCount = config.getInt("freecam_trigger_count", freecamTriggerCount);
        preprocessLogEnabled = config.getBoolean("preprocess_log", preprocessLogEnabled);
        disableNight = config.getBoolean("disable_night", disableNight);
        disableWeather = config.getBoolean("disable_weather", disableWeather);
    }
    
    public List<String> superadmins = new ArrayList<String>();
    public List<String> superadmin_ips = new ArrayList<String>();
    
    public void loadSuperadminConfig()
    {
        TFM_Util.createDefaultConfiguration(SUPERADMIN_FILE, this, getFile());
        FileConfiguration sa_config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), SUPERADMIN_FILE));

        superadmins = new ArrayList<String>();
        List<String> superadmins_temp = (List<String>) sa_config.getList("superadmins", null);
        if (superadmins_temp == null || superadmins_temp.isEmpty())
        {
            superadmins.add("Madgeek1450");
            superadmins.add("markbyron");
        }
        else
        {
            for (String admin_name : superadmins_temp)
            {
                superadmins.add(admin_name.toLowerCase().trim());
            }
        }
        
        superadmin_ips = new ArrayList<String>();
        List<String> superadmin_ips_temp = (List<String>) sa_config.getList("superadmin_ips", null);
        if (superadmin_ips_temp == null || superadmin_ips_temp.isEmpty())
        {
            superadmin_ips.add("127.0.0.1");
        }
        else
        {
            for (String admin_ip : superadmin_ips_temp)
            {
                superadmin_ips.add(admin_ip.toLowerCase().trim());
            }
        }
    }
    
    private final TFM_EntityListener entityListener = new TFM_EntityListener(this);
    private final TFM_BlockListener blockListener = new TFM_BlockListener(this);
    private final TFM_PlayerListener playerListener = new TFM_PlayerListener(this);
    private final TFM_WeatherListener weatherListener = new TFM_WeatherListener(this);
    
    private void registerEventHandlers()
    {
        PluginManager pm = this.getServer().getPluginManager();
        
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.EXPLOSION_PRIME, entityListener, Event.Priority.High, this);

        pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);

        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Monitor, this);
        
        pm.registerEvent(Event.Type.WEATHER_CHANGE, weatherListener, Event.Priority.High, this);
        pm.registerEvent(Event.Type.THUNDER_CHANGE, weatherListener, Event.Priority.High, this);
    }
}
