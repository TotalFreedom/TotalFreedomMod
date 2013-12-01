package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_Command;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandLoader;
import me.StevenLawson.TotalFreedomMod.HTTPD.TFM_HTTPD_Manager;
import me.StevenLawson.TotalFreedomMod.Listener.*;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import net.minecraft.util.org.apache.commons.lang3.exception.ExceptionUtils;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.Metrics;

public class TotalFreedomMod extends JavaPlugin
{
    public static final long HEARTBEAT_RATE = 5L; //Seconds
    public static final long SERVICE_CHECKER_RATE = 120L;
    //
    public static final String SUPERADMIN_FILE = "superadmin.yml";
    public static final String PERMBAN_FILE = "permban.yml";
    public static final String PROTECTED_AREA_FILE = "protectedareas.dat";
    public static final String SAVED_FLAGS_FILE = "savedflags.dat";
    //
    public static final String COMMAND_PATH = "me.StevenLawson.TotalFreedomMod.Commands";
    public static final String COMMAND_PREFIX = "Command_";
    //
    public static final String MSG_NO_PERMS = ChatColor.YELLOW + "You do not have permission to use this command.";
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String CAKE_LYRICS = "But there's no sense crying over every mistake. You just keep on trying till you run out of cake.";
    public static final String NOT_FROM_CONSOLE = "This command may not be used from the console.";
    //
    public static final Server server = Bukkit.getServer();
    public static TotalFreedomMod plugin = null;
    //
    public static String pluginName = "";
    public static String pluginVersion = "";
    public static String buildNumber = "";
    public static String buildDate = "";
    //
    public static boolean allPlayersFrozen = false;
    public static BukkitTask freezePurgeTask = null;
    public static BukkitTask mutePurgeTask = null;
    public static boolean lockdownEnabled = false;
    public static Map<Player, Double> fuckoffEnabledFor = new HashMap<Player, Double>();
    //
    public static List<String> permbannedPlayers = new ArrayList<String>();
    public static List<String> permbannedIps = new ArrayList<String>();

    @Override
    public void onLoad()
    {
        TotalFreedomMod.plugin = this;
        TotalFreedomMod.pluginName = plugin.getDescription().getName();
        TotalFreedomMod.pluginVersion = plugin.getDescription().getVersion();

        TFM_Log.setPluginLogger(plugin.getLogger());
        TFM_Log.setServerLogger(server.getLogger());

        setAppProperties();
    }

    @Override
    public void onEnable()
    {
        TFM_Log.info("Version: " + TotalFreedomMod.pluginVersion + "." + TotalFreedomMod.buildNumber + " by Madgeek1450 and DarthSalamon");

        loadSuperadminConfig();
        loadPermbanConfig();

        TFM_UserList.getInstance(plugin);

        registerEventHandlers();

        try
        {
            TFM_Flatlands.getInstance().getWorld();
        }
        catch (Exception ex)
        {
        }

        try
        {
            TFM_AdminWorld.getInstance().getWorld();
        }
        catch (Exception ex)
        {
        }

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

        // Initialize game rules
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_DAYLIGHT_CYCLE, !TFM_ConfigEntry.DISABLE_NIGHT.getBoolean(), false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_FIRE_TICK, TFM_ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean(), false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_MOB_LOOT, false, false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_MOB_SPAWNING, !TFM_ConfigEntry.MOB_LIMITER_ENABLED.getBoolean(), false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_TILE_DROPS, false, false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.MOB_GRIEFING, false, false);
        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.NATURAL_REGENERATION, true, false);
        TFM_GameRuleHandler.commitGameRules();

        if (TFM_ConfigEntry.PROTECTED_AREAS_ENABLED.getBoolean())
        {
            TFM_ProtectedArea.loadProtectedAreas();
            TFM_ProtectedArea.autoAddSpawnpoints();
        }

        TFM_Util.deleteFolder(new File("./_deleteme"));

        File[] coreDumps = new File(".").listFiles(new java.io.FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                return file.getName().startsWith("java.core");
            }
        });

        for (File dump : coreDumps)
        {
            TFM_Log.info("Removing core dump file: " + dump.getName());
            dump.delete();
        }

        // Heartbeat
        new TFM_Heartbeat(plugin).runTaskTimer(plugin, HEARTBEAT_RATE * 20L, HEARTBEAT_RATE * 20L);

        // metrics @ http://mcstats.org/plugin/TotalFreedomMod
        try
        {
            Metrics metrics = new Metrics(plugin);
            metrics.start();
        }
        catch (IOException ex)
        {
            TFM_Log.warning("Failed to submit metrics data: " + ex.getMessage());
        }

        TFM_ServiceChecker.getInstance().start();
        TFM_HTTPD_Manager.getInstance().start();
        TFM_FrontDoor.getInstance().start();

        TFM_Log.info("Version " + pluginVersion + " enabled");

        // Delayed Start :
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                TFM_CommandLoader.getInstance().scan();
                TFM_CommandBlocker.getInstance().parseBlockingRules();
            }
        }.runTaskLater(plugin, 20L);
    }

    @Override
    public void onDisable()
    {
        server.getScheduler().cancelTasks(plugin);

        TFM_HTTPD_Manager.getInstance().stop();

        TFM_Log.info("Plugin disabled");
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
                        StringUtils.join(args, " ")), true);
            }
            else
            {
                senderIsConsole = true;
                TFM_Log.info(String.format("[CONSOLE_COMMAND] %s: /%s %s",
                        sender.getName(),
                        commandLabel,
                        StringUtils.join(args, " ")), true);
            }

            TFM_Command dispatcher;
            try
            {
                ClassLoader classLoader = TotalFreedomMod.class.getClassLoader();
                dispatcher = (TFM_Command) classLoader.loadClass(String.format("%s.%s%s", COMMAND_PATH, COMMAND_PREFIX, cmd.getName().toLowerCase())).newInstance();
                dispatcher.setup(plugin, sender, dispatcher.getClass());
            }
            catch (Throwable ex)
            {
                TFM_Log.severe("Command not loaded: " + cmd.getName() + "\n" + ExceptionUtils.getStackTrace(ex));
                sender.sendMessage(ChatColor.RED + "Command Error: Command not loaded: " + cmd.getName());
                return true;
            }

            try
            {
                if (dispatcher.senderHasPermission())
                {
                    return dispatcher.run(sender, sender_p, cmd, commandLabel, args, senderIsConsole);
                }
                else
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                }
            }
            catch (Throwable ex)
            {
                TFM_Log.severe("Command Error: " + commandLabel + "\n" + ExceptionUtils.getStackTrace(ex));
                sender.sendMessage(ChatColor.RED + "Command Error: " + ex.getMessage());
            }

        }
        catch (Throwable ex)
        {
            TFM_Log.severe("Command Error: " + commandLabel + "\n" + ExceptionUtils.getStackTrace(ex));
            sender.sendMessage(ChatColor.RED + "Unknown Command Error.");
        }

        return true;
    }

    public static void loadSuperadminConfig()
    {
        try
        {
            TFM_SuperadminList.backupSavedList();
            TFM_SuperadminList.loadSuperadminList();
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Error loading superadmin list: " + ex.getMessage());
        }
    }

    public static void loadPermbanConfig()
    {
        try
        {
            TFM_Util.createDefaultConfiguration(PERMBAN_FILE);
            FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), PERMBAN_FILE));

            permbannedPlayers = new ArrayList<String>();
            permbannedIps = new ArrayList<String>();

            for (String user : config.getKeys(false))
            {
                permbannedPlayers.add(user.toLowerCase().trim());

                List<String> user_ips = config.getStringList(user);
                for (String ip : user_ips)
                {
                    ip = ip.toLowerCase().trim();
                    if (!permbannedIps.contains(ip))
                    {
                        permbannedIps.add(ip);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Error loading permban list!");
            TFM_Log.severe(ex);
        }
    }

    private static void registerEventHandlers()
    {
        PluginManager pm = server.getPluginManager();

        pm.registerEvents(new TFM_EntityListener(), plugin);
        pm.registerEvents(new TFM_BlockListener(), plugin);
        pm.registerEvents(new TFM_PlayerListener(), plugin);
        pm.registerEvents(new TFM_WeatherListener(), plugin);
        pm.registerEvents(new TFM_ServerListener(), plugin);
        pm.registerEvents(new TFM_CustomListener(), plugin);
    }

    private static void setAppProperties()
    {
        try
        {
            InputStream in = plugin.getResource("appinfo.properties");
            Properties props = new Properties();

            // in = plugin.getClass().getResourceAsStream("/appinfo.properties");
            props.load(in);
            in.close();

            TotalFreedomMod.buildNumber = props.getProperty("program.buildnumber");
            TotalFreedomMod.buildDate = props.getProperty("program.builddate");
        }
        catch (Exception ex)
        {
            TFM_Log.severe("Could not load App properties!");
            TFM_Log.severe(ex);

            TotalFreedomMod.buildNumber = "1";
            TotalFreedomMod.buildDate = TFM_Util.dateToString(new Date());
        }
    }
}
