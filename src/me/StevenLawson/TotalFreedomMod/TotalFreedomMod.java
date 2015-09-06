package me.StevenLawson.TotalFreedomMod;

import com.google.common.base.Function;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import me.StevenLawson.TotalFreedomMod.Bridge.TFM_BukkitTelnetListener;
import me.StevenLawson.TotalFreedomMod.Bridge.TFM_WorldEditListener;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandHandler;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandLoader;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.HTTPD.TFM_HTTPD_Manager;
import me.StevenLawson.TotalFreedomMod.Listener.TFM_BlockListener;
import me.StevenLawson.TotalFreedomMod.Listener.TFM_EntityListener;
import me.StevenLawson.TotalFreedomMod.Listener.TFM_PlayerListener;
import me.StevenLawson.TotalFreedomMod.Listener.TFM_ServerListener;
import me.StevenLawson.TotalFreedomMod.Listener.TFM_WeatherListener;
import me.StevenLawson.TotalFreedomMod.World.TFM_AdminWorld;
import me.StevenLawson.TotalFreedomMod.World.TFM_Flatlands;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.Metrics;

public class TotalFreedomMod extends JavaPlugin
{
    public static final long HEARTBEAT_RATE = 5L; // Seconds
    public static final long SERVICE_CHECKER_RATE = 120L;
    public static final int MAX_USERNAME_LENGTH = 20;
    //
    public static final String CONFIG_FILENAME = "config.yml";
    public static final String SUPERADMIN_FILENAME = "superadmin.yml";
    public static final String PERMBAN_FILENAME = "permban.yml";
    public static final String UUID_FILENAME = "uuids.db";
    public static final String PROTECTED_AREA_FILENAME = "protectedareas.dat";
    public static final String SAVED_FLAGS_FILENAME = "savedflags.dat";
    //
    @Deprecated
    public static final String YOU_ARE_NOT_OP = me.StevenLawson.TotalFreedomMod.Commands.TFM_Command.YOU_ARE_NOT_OP;
    //
    public static String buildNumber = "1";
    public static String buildDate = TotalFreedomMod.buildDate = TFM_Util.dateToString(new Date());
    public static String buildCreator = "Unknown";
    //
    public static Server server;
    public static TotalFreedomMod plugin;
    public static String pluginName;
    public static String pluginVersion;
    //
    public static boolean lockdownEnabled = false;
    public static Map<Player, Double> fuckoffEnabledFor = new HashMap<Player, Double>();

    @Override
    public void onLoad()
    {
        TotalFreedomMod.plugin = this;
        TotalFreedomMod.server = plugin.getServer();
        TotalFreedomMod.pluginName = plugin.getDescription().getName();
        TotalFreedomMod.pluginVersion = plugin.getDescription().getVersion();

        TFM_Log.setPluginLogger(plugin.getLogger());
        TFM_Log.setServerLogger(server.getLogger());

        setAppProperties();
    }

    @Override
    public void onEnable()
    {
        TFM_Log.info("Made by Madgeek1450 and Prozza");
        TFM_Log.info("Compiled " + buildDate + " by " + buildCreator);

        final TFM_Util.MethodTimer timer = new TFM_Util.MethodTimer();
        timer.start();

        if (!TFM_ServerInterface.COMPILE_NMS_VERSION.equals(TFM_Util.getNmsVersion()))
        {
            TFM_Log.warning(pluginName + " is compiled for " + TFM_ServerInterface.COMPILE_NMS_VERSION + " but the server is running "
                    + "version " + TFM_Util.getNmsVersion() + "!");
            TFM_Log.warning("This might result in unexpected behaviour!");
        }

        TFM_Util.deleteCoreDumps();
        TFM_Util.deleteFolder(new File("./_deleteme"));

        // Create backups
        TFM_Util.createBackups(CONFIG_FILENAME, true);
        TFM_Util.createBackups(SUPERADMIN_FILENAME);
        TFM_Util.createBackups(PERMBAN_FILENAME);

        // Load services
        TFM_UuidManager.load();
        TFM_AdminList.load();
        TFM_PermbanList.load();
        TFM_PlayerList.load();
        TFM_BanManager.load();
        TFM_Announcer.load();
        TFM_ProtectedArea.load();

        // Start SuperAdmin service
        server.getServicesManager().register(Function.class, TFM_AdminList.SUPERADMIN_SERVICE, plugin, ServicePriority.Normal);

        final PluginManager pm = server.getPluginManager();
        pm.registerEvents(new TFM_EntityListener(), plugin);
        pm.registerEvents(new TFM_BlockListener(), plugin);
        pm.registerEvents(new TFM_PlayerListener(), plugin);
        pm.registerEvents(new TFM_WeatherListener(), plugin);
        pm.registerEvents(new TFM_ServerListener(), plugin);

        // Bridge
        pm.registerEvents(new TFM_BukkitTelnetListener(), plugin);
        pm.registerEvents(new TFM_WorldEditListener(), plugin);

        try
        {
            TFM_Flatlands.getInstance().getWorld();
        }
        catch (Exception ex)
        {
            TFM_Log.warning("Could not load world: Flatlands");
        }

        try
        {
            TFM_AdminWorld.getInstance().getWorld();
        }
        catch (Exception ex)
        {
            TFM_Log.warning("Could not load world: AdminWorld");
        }

        // Initialize game rules
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_DAYLIGHT_CYCLE, !TFM_ConfigEntry.DISABLE_NIGHT.getBoolean(), false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_FIRE_TICK, TFM_ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean(), false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_MOB_LOOT, false, false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_MOB_SPAWNING, !TFM_ConfigEntry.MOB_LIMITER_ENABLED.getBoolean(), false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_TILE_DROPS, false, false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.MOB_GRIEFING, false, false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.NATURAL_REGENERATION, true, false);
        TFM_GameRuleHandler.commitGameRules();

        // Disable weather
        if (TFM_ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            for (World world : server.getWorlds())
            {
                world.setThundering(false);
                world.setStorm(false);
                world.setThunderDuration(0);
                world.setWeatherDuration(0);
            }
        }

        // Heartbeat
        new TFM_Heartbeat(plugin).runTaskTimer(plugin, HEARTBEAT_RATE * 20L, HEARTBEAT_RATE * 20L);

        // Start services
        TFM_ServiceChecker.start();
        TFM_HTTPD_Manager.start();
        TFM_FrontDoor.start();
        TFM_CommandBlocker.load();

        timer.update();

        TFM_Log.info("Version " + pluginVersion + " for " + TFM_ServerInterface.COMPILE_NMS_VERSION + " enabled in " + timer.getTotal() + "ms");

        // Metrics @ http://mcstats.org/plugin/TotalFreedomMod
        try
        {
            final Metrics metrics = new Metrics(plugin);
            metrics.start();
        }
        catch (IOException ex)
        {
            TFM_Log.warning("Failed to submit metrics data: " + ex.getMessage());
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                TFM_CommandLoader.scan();

                // Add spawnpoints later - https://github.com/TotalFreedom/TotalFreedomMod/issues/438
                TFM_ProtectedArea.autoAddSpawnpoints();
            }
        }.runTaskLater(plugin, 20L);
    }

    @Override
    public void onDisable()
    {
        TFM_HTTPD_Manager.stop();
        TFM_BanManager.save();
        TFM_UuidManager.close();
        TFM_FrontDoor.stop();

        server.getScheduler().cancelTasks(plugin);

        TFM_Log.info("Plugin disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return TFM_CommandHandler.handleCommand(sender, cmd, commandLabel, args);
    }

    private static void setAppProperties()
    {
        try
        {
            final InputStream in = plugin.getResource("appinfo.properties");
            Properties props = new Properties();

            // in = plugin.getClass().getResourceAsStream("/appinfo.properties");
            props.load(in);
            in.close();

            TotalFreedomMod.buildNumber = props.getProperty("program.buildnumber");
            TotalFreedomMod.buildDate = props.getProperty("program.builddate");
            TotalFreedomMod.buildCreator = props.getProperty("program.buildcreator");
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Could not load App properties!");
            TFM_Log.severe(ex);
        }
    }
}
