package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_Command;
import me.StevenLawson.TotalFreedomMod.Listener.TFM_BlockListener;
import me.StevenLawson.TotalFreedomMod.Listener.TFM_EntityListener;
import me.StevenLawson.TotalFreedomMod.Listener.TFM_PlayerListener;
import me.StevenLawson.TotalFreedomMod.Listener.TFM_WeatherListener;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TotalFreedomMod extends JavaPlugin
{
    private final Server server = Bukkit.getServer();

    public static final long HEARTBEAT_RATE = 5L; //Seconds

    public static final String CONFIG_FILE = "config.yml";
    public static final String SUPERADMIN_FILE = "superadmin.yml";
    public static final String PERMBAN_FILE = "permban.yml";
    public static final String PROTECTED_AREA_FILE = "protectedareas.dat";
    public static final String SAVED_FLAGS_FILE = "savedflags.dat";

    public static final String COMMAND_PATH = "me.StevenLawson.TotalFreedomMod.Commands";
    public static final String COMMAND_PREFIX = "Command_";

    public static final String MSG_NO_PERMS = ChatColor.YELLOW + "You do not have permission to use this command.";
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String CAKE_LYRICS = "But there's no sense crying over every mistake. You just keep on trying till you run out of cake.";
    public static final String NOT_FROM_CONSOLE = "This command may not be used from the console.";

    public static boolean allPlayersFrozen = false;
    public static Map<Player, Double> fuckoffEnabledFor = new HashMap<Player, Double>();

    public static String pluginVersion = "";
    public static String buildNumber = "";
    public static String buildDate = "";
    public static String pluginName = "";

    public static TotalFreedomMod plugin = null;
    public static File plugin_file = null;

    @Override
    public void onEnable()
    {
        TotalFreedomMod.plugin = this;
        TotalFreedomMod.plugin_file = getFile();

        TotalFreedomMod.pluginName = this.getDescription().getName();

        setAppProperties();

        loadMainConfig();
        loadSuperadminConfig();
        loadPermbanConfig();

        TFM_UserList.getInstance(this);

        registerEventHandlers();

        server.getScheduler().scheduleAsyncRepeatingTask(this, new TFM_Heartbeat(this), HEARTBEAT_RATE * 20L, HEARTBEAT_RATE * 20L);

        TFM_Log.info("Plugin Enabled - Version: " + TotalFreedomMod.pluginVersion + "." + TotalFreedomMod.buildNumber + " by Madgeek1450 and DarthSalamon");

        TFM_Util.deleteFolder(new File("./_deleteme"));

        if (generateFlatlands)
        {
            TFM_Util.wipeFlatlandsIfFlagged();
            TFM_Util.generateFlatlands(flatlandsGenerationParams);
        }

        if (disableWeather)
        {
            for (World world : server.getWorlds())
            {
                world.setThundering(false);
                world.setStorm(false);
                world.setThunderDuration(0);
                world.setThunderDuration(0);
            }
        }

        if (TotalFreedomMod.protectedAreasEnabled)
        {
            TFM_ProtectedArea.loadProtectedAreas();
            TFM_ProtectedArea.autoAddSpawnpoints();
        }
    }

    @Override
    public void onDisable()
    {
        server.getScheduler().cancelTasks(this);
        TFM_Log.info("Plugin Disabled");
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
                TFM_Log.info(String.format("[PLAYER_COMMAND] %s(%s): /%s %s",
                        sender_p.getName(),
                        ChatColor.stripColor(sender_p.getDisplayName()),
                        commandLabel,
                        TFM_Util.implodeStringList(" ", Arrays.asList(args))), true);
            }
            else
            {
                senderIsConsole = true;
                TFM_Log.info(String.format("[CONSOLE_COMMAND] %s: /%s %s",
                        sender.getName(),
                        commandLabel,
                        TFM_Util.implodeStringList(" ", Arrays.asList(args))), true);
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
                TFM_Log.severe("Command not loaded: " + cmd.getName() + "\n" + ExceptionUtils.getStackTrace(ex));
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

            dispatcher = null;
        }
        catch (Throwable ex)
        {
            TFM_Log.severe("Command Error: " + commandLabel + "\n" + ExceptionUtils.getStackTrace(ex));
            sender.sendMessage(ChatColor.RED + "Unknown Command Error.");
        }

        return true;
    }

    public static boolean allowFirePlace = false;
    public static Boolean allowFireSpread = false;
    public static Boolean allowLavaDamage = false;
    public static boolean allowLavaPlace = false;
    public static boolean allowWaterPlace = false;
    public static Boolean allowExplosions = false;
    public static double explosiveRadius = 4.0D;
    public static boolean autoEntityWipe = true;
    public static boolean nukeMonitor = true;
    public static int nukeMonitorCountBreak = 100;
    public static int nukeMonitorCountPlace = 25;
    public static double nukeMonitorRange = 10.0D;
    public static int freecamTriggerCount = 10;
    public static Boolean preprocessLogEnabled = true;
    public static Boolean disableNight = true;
    public static Boolean disableWeather = true;
    public static boolean landminesEnabled = false;
    public static boolean mp44Enabled = false;
    public static boolean mobLimiterEnabled = true;
    public static int mobLimiterMax = 50;
    public static boolean mobLimiterDisableDragon = true;
    public static boolean mobLimiterDisableGhast = true;
    public static boolean mobLimiterDisableSlime = true;
    public static boolean mobLimiterDisableGiant = true;
    public static boolean tossmobEnabled = false;
    public static boolean generateFlatlands = true;
    public static String flatlandsGenerationParams = "16,stone,32,dirt,1,grass";
    public static boolean allowFliudSpread = false;
    public static boolean adminOnlyMode = false;
    public static boolean protectedAreasEnabled = true;
    public static boolean autoProtectSpawnpoints = true;
    public static double autoProtectRadius = 25.0D;

    public void loadMainConfig()
    {
        try
        {
            TFM_Util.createDefaultConfiguration(CONFIG_FILE, getFile());
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
            landminesEnabled = config.getBoolean("landmines_enabled", landminesEnabled);
            mp44Enabled = config.getBoolean("mp44_enabled", mp44Enabled);
            mobLimiterEnabled = config.getBoolean("mob_limiter_enabled", mobLimiterEnabled);
            mobLimiterMax = config.getInt("mob_limiter_max", mobLimiterMax);
            mobLimiterDisableDragon = config.getBoolean("mob_limiter_disable_dragon", mobLimiterDisableDragon);
            mobLimiterDisableGhast = config.getBoolean("mob_limiter_disable_ghast", mobLimiterDisableGhast);
            mobLimiterDisableSlime = config.getBoolean("mob_limiter_disable_slime", mobLimiterDisableSlime);
            mobLimiterDisableGiant = config.getBoolean("mob_limiter_disable_giant", mobLimiterDisableGiant);
            tossmobEnabled = config.getBoolean("tossmob_enabled", tossmobEnabled);
            generateFlatlands = config.getBoolean("generate_flatlands", generateFlatlands);
            flatlandsGenerationParams = config.getString("flatlands_generation_params", flatlandsGenerationParams);
            allowFliudSpread = config.getBoolean("allow_fluid_spread", allowFliudSpread);
            adminOnlyMode = config.getBoolean("admin_only_mode", adminOnlyMode);
            protectedAreasEnabled = config.getBoolean("protected_areas_enabled", protectedAreasEnabled);
            autoProtectSpawnpoints = config.getBoolean("auto_protect_spawnpoints", autoProtectSpawnpoints);
            autoProtectRadius = config.getDouble("auto_protect_radius", autoProtectRadius);
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Error loading main config: " + ex.getMessage());
        }
    }

    @Deprecated
    public static List<String> superadmins = new ArrayList<String>();
    @Deprecated
    public static List<String> superadmin_ips = new ArrayList<String>();

    public void loadSuperadminConfig()
    {
        try
        {
            TFM_SuperadminList.loadSuperadminList();

            superadmins = TFM_SuperadminList.getSuperadminNames();
            superadmin_ips = TFM_SuperadminList.getSuperadminIPs();
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Error loading superadmin list: " + ex.getMessage());
        }
    }

    public static List<String> permbanned_players = new ArrayList<String>();
    public static List<String> permbanned_ips = new ArrayList<String>();

    public void loadPermbanConfig()
    {
        try
        {
            TFM_Util.createDefaultConfiguration(PERMBAN_FILE, getFile());
            FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), PERMBAN_FILE));

            permbanned_players = new ArrayList<String>();
            permbanned_ips = new ArrayList<String>();

            for (String user : config.getKeys(false))
            {
                permbanned_players.add(user.toLowerCase().trim());

                List<String> user_ips = (List<String>) config.getStringList(user);
                for (String ip : user_ips)
                {
                    ip = ip.toLowerCase().trim();
                    if (!permbanned_ips.contains(ip))
                    {
                        permbanned_ips.add(ip);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Error loading permban list: " + ex.getMessage());
        }
    }

    private void registerEventHandlers()
    {
        PluginManager pm = server.getPluginManager();

        pm.registerEvents(new TFM_EntityListener(), plugin);
        pm.registerEvents(new TFM_BlockListener(), plugin);
        pm.registerEvents(new TFM_PlayerListener(), plugin);
        pm.registerEvents(new TFM_WeatherListener(), plugin);
    }

    private void setAppProperties()
    {
        try
        {
            InputStream in;
            Properties props = new Properties();

            in = getClass().getResourceAsStream("/appinfo.properties");
            props.load(in);
            in.close();

            TotalFreedomMod.pluginVersion = props.getProperty("program.VERSION");
            TotalFreedomMod.buildNumber = props.getProperty("program.BUILDNUM");
            TotalFreedomMod.buildDate = props.getProperty("program.BUILDDATE");
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }
}
