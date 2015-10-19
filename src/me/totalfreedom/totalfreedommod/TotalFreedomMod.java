package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.PermbanList;
import me.totalfreedom.totalfreedommod.ProtectArea;
import me.totalfreedom.totalfreedommod.ServiceChecker;
import me.totalfreedom.totalfreedommod.ServerInterface;
import me.totalfreedom.totalfreedommod.Jumppads;
import me.totalfreedom.totalfreedommod.GameRuleHandler;
import me.totalfreedom.totalfreedommod.Heartbeat;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.FrontDoor;
import me.totalfreedom.totalfreedommod.Announcer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import me.totalfreedom.totalfreedommod.admin.AdminList;
import me.totalfreedom.totalfreedommod.banning.BanManager;
import me.totalfreedom.totalfreedommod.bridge.BukkitTelnetBridge;
import me.totalfreedom.totalfreedommod.bridge.EssentialsBridge;
import me.totalfreedom.totalfreedommod.bridge.WorldEditBridge;
import me.totalfreedom.totalfreedommod.commandblocker.CommandBlocker;
import me.totalfreedom.totalfreedommod.commands.CommandLoader;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.listener.BlockListener;
import me.totalfreedom.totalfreedommod.listener.EntityListener;
import me.totalfreedom.totalfreedommod.listener.PlayerListener;
import me.totalfreedom.totalfreedommod.listener.ServerListener;
import me.totalfreedom.totalfreedommod.listener.WeatherListener;
import me.totalfreedom.totalfreedommod.permission.RankManager;
import me.totalfreedom.totalfreedommod.player.PlayerList;
import me.totalfreedom.totalfreedommod.rollback.RollbackManager;
import me.totalfreedom.totalfreedommod.world.TFM_WorldManager;
import net.pravian.aero.component.service.ServiceManager;
import net.pravian.aero.plugin.AeroPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.Metrics;

public class TotalFreedomMod extends AeroPlugin<TotalFreedomMod>
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
    public static final BuildProperties build = new BuildProperties();
    //
    public static TotalFreedomMod plugin;
    public static String pluginName;
    public static String pluginVersion;
    //
    public static boolean lockdownEnabled = false;
    public static Map<Player, Double> fuckoffEnabledFor = new HashMap<Player, Double>();
    //
    // Services
    public ServiceManager<TotalFreedomMod> services;
    public ServerInterface si;
    public AdminList al;
    public RankManager rm;
    public BanManager bm;
    public PlayerList pl;
    public CommandLoader cl;
    public CommandBlocker cb;
    public Announcer an;
    public PermbanList pb;
    public ProtectArea pa;
    public ServiceChecker sc;
    public GameRuleHandler gr;
    public RollbackManager rb;
    public Heartbeat hb;
    public Jumppads jp;
    public FrontDoor fd;
    public HTTPDaemon hd;
    public TFM_WorldManager wm;
    //
    // Bridges
    public ServiceManager<TotalFreedomMod> bridges;
    public BukkitTelnetBridge btb;
    public EssentialsBridge esb;
    public WorldEditBridge web;

    @Override
    public void load()
    {
        TotalFreedomMod.plugin = this;
        TotalFreedomMod.pluginName = plugin.getDescription().getName();
        TotalFreedomMod.pluginVersion = plugin.getDescription().getVersion();

        FLog.setPluginLogger(plugin.getLogger());
        FLog.setServerLogger(server.getLogger());

        build.load();

        services = new ServiceManager<TotalFreedomMod>(plugin);
        si = services.registerService(ServerInterface.class);
        al = services.registerService(AdminList.class);
        rm = services.registerService(RankManager.class);
        bm = services.registerService(BanManager.class);
        pl = services.registerService(PlayerList.class);
        cl = services.registerService(CommandLoader.class);
        cb = services.registerService(CommandBlocker.class);
        an = services.registerService(Announcer.class);
        pb = services.registerService(PermbanList.class);
        pa = services.registerService(ProtectArea.class);
        sc = services.registerService(ServiceChecker.class);
        gr = services.registerService(GameRuleHandler.class);
        rb = services.registerService(RollbackManager.class);
        hb = services.registerService(Heartbeat.class);
        jp = services.registerService(Jumppads.class);
        fd = services.registerService(FrontDoor.class);
        hd = services.registerService(HTTPDaemon.class);
        wm = services.registerService(TFM_WorldManager.class);

        bridges = new ServiceManager<TotalFreedomMod>(plugin);
        btb = bridges.registerService(BukkitTelnetBridge.class);
        esb = bridges.registerService(EssentialsBridge.class);
        web = bridges.registerService(WorldEditBridge.class);
    }

    @Override
    public void enable()
    {
        TotalFreedomMod.plugin = this;

        FLog.info("Created by Madgeek1450 and Prozza");
        FLog.info("Version " + build.formattedVersion());
        FLog.info("Compiled " + build.date + " by " + build.builder);

        final FUtil.MethodTimer timer = new FUtil.MethodTimer();
        timer.start();

        if (!ServerInterface.COMPILE_NMS_VERSION.equals(FUtil.getNmsVersion()))
        {
            FLog.warning(pluginName + " is compiled for " + ServerInterface.COMPILE_NMS_VERSION + " but the server is running "
                    + "version " + FUtil.getNmsVersion() + "!");
            FLog.warning("This might result in unexpected behaviour!");
        }

        FUtil.deleteCoreDumps();
        FUtil.deleteFolder(new File("./_deleteme"));

        // Create backups
        FUtil.createBackups(CONFIG_FILENAME, true);
        FUtil.createBackups(SUPERADMIN_FILENAME);
        FUtil.createBackups(PERMBAN_FILENAME);

        // Start services and bridges
        services.start();
        bridges.start();

        // Register listeners
        register(EntityListener.class);
        register(BlockListener.class);
        register(PlayerListener.class);
        register(WeatherListener.class);
        register(ServerListener.class);

        // Disable weather
        if (ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            for (World world : server.getWorlds())
            {
                world.setThundering(false);
                world.setStorm(false);
                world.setThunderDuration(0);
                world.setWeatherDuration(0);
            }
        }

        timer.update();

        FLog.info("Version " + pluginVersion + " for " + ServerInterface.COMPILE_NMS_VERSION + " enabled in " + timer.getTotal() + "ms");

        // Metrics @ http://mcstats.org/plugin/TotalFreedomMod
        try
        {
            final Metrics metrics = new Metrics(plugin);
            metrics.start();
        }
        catch (IOException ex)
        {
            FLog.warning("Failed to submit metrics data: " + ex.getMessage());
        }

        // Add spawnpoints later - https://github.com/TotalFreedom/TotalFreedomMod/issues/438
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                plugin.pa.autoAddSpawnpoints();
            }
        }.runTaskLater(plugin, 60L);
    }

    @Override
    public void disable()
    {
        // Stop services and bridges
        services.stop();
        bridges.stop();

        server.getScheduler().cancelTasks(plugin);

        FLog.info("Plugin disabled");
        TotalFreedomMod.plugin = null;
    }

    public static class BuildProperties
    {
        public String builder;
        public String number;
        public String head;
        public String date;

        public void load()
        {
            try
            {
                final InputStream in = plugin.getResource("build.properties");

                final Properties props = new Properties();
                props.load(in);
                in.close();

                builder = props.getProperty("program.builder", "unknown");
                number = props.getProperty("program.buildnumber", "1");
                head = props.getProperty("program.buildhead", "unknown");
                date = props.getProperty("program.builddate", "unknown");

            }
            catch (Exception ex)
            {
                FLog.severe("Could not load build properties! Did you compile with Netbeans/ANT?");
                FLog.severe(ex);
            }
        }

        public String formattedVersion()
        {
            return pluginVersion + "." + number + " (" + head + ")";
        }
    }

}
