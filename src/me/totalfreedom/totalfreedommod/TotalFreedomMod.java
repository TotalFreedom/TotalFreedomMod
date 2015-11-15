package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.blocking.MobBlocker;
import me.totalfreedom.totalfreedommod.banning.PermbanList;
import me.totalfreedom.totalfreedommod.fun.Jumppads;
import me.totalfreedom.totalfreedommod.fun.MP44;
import me.totalfreedom.totalfreedommod.fun.ItemFun;
import me.totalfreedom.totalfreedommod.blocking.InteractBlocker;
import me.totalfreedom.totalfreedommod.blocking.EventBlocker;
import me.totalfreedom.totalfreedommod.blocking.BlockBlocker;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import me.totalfreedom.totalfreedommod.admin.AdminList;
import me.totalfreedom.totalfreedommod.banning.BanManager;
import me.totalfreedom.totalfreedommod.bridge.BukkitTelnetBridge;
import me.totalfreedom.totalfreedommod.bridge.EssentialsBridge;
import me.totalfreedom.totalfreedommod.bridge.WorldEditBridge;
import me.totalfreedom.totalfreedommod.caging.Cager;
import me.totalfreedom.totalfreedommod.blocking.command.CommandBlocker;
import me.totalfreedom.totalfreedommod.commands.CommandLoader;
import me.totalfreedom.totalfreedommod.freeze.Freezer;
import me.totalfreedom.totalfreedommod.fun.Landminer;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.rank.RankManager;
import me.totalfreedom.totalfreedommod.player.PlayerList;
import me.totalfreedom.totalfreedommod.rollback.RollbackManager;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.world.WorldManager;
import net.pravian.aero.component.service.ServiceManager;
import net.pravian.aero.plugin.AeroPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.Metrics;

public class TotalFreedomMod extends AeroPlugin<TotalFreedomMod>
{
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
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static TotalFreedomMod plugin;
    public static String pluginName;
    public static String pluginVersion;
    //
    public static boolean lockdownEnabled = false;
    //
    // Services
    public ServiceManager<TotalFreedomMod> services;
    public ServerInterface si;
    public ConsoleLogger co;
    public WorldManager wm;
    public AdminList al;
    public EventBlocker eb;
    public BlockBlocker bb;
    public MobBlocker mb;
    public InteractBlocker ib;
    public LoginProcess lp;
    public AntiNuke nu;
    public AntiSpam as;
    public Muter mu;
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
    public Jumppads jp;
    public Cager ca;
    public Freezer fm;
    public Fuckoff fo;
    public EntityWiper ew;
    public FrontDoor fd;
    public ServerPing sp;
    public ItemFun it;
    public Landminer lm;
    public MP44 mp;
    public HTTPDaemon hd;
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

        // Start services and bridgess
        services = new ServiceManager<TotalFreedomMod>(plugin);
        si = services.registerService(ServerInterface.class);
        co = services.registerService(ConsoleLogger.class);
        wm = services.registerService(WorldManager.class);
        al = services.registerService(AdminList.class);
        eb = services.registerService(EventBlocker.class);
        bb = services.registerService(BlockBlocker.class);
        mb = services.registerService(MobBlocker.class);
        ib = services.registerService(InteractBlocker.class);
        lp = services.registerService(LoginProcess.class);
        nu = services.registerService(AntiNuke.class);
        as = services.registerService(AntiSpam.class);
        mu = services.registerService(Muter.class);
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
        jp = services.registerService(Jumppads.class);
        ca = services.registerService(Cager.class);
        fm = services.registerService(Freezer.class);
        fo = services.registerService(Fuckoff.class);
        ew = services.registerService(EntityWiper.class);
        fd = services.registerService(FrontDoor.class);
        sp = services.registerService(ServerPing.class);
        it = services.registerService(ItemFun.class);
        lm = services.registerService(Landminer.class);
        mp = services.registerService(MP44.class);
        hd = services.registerService(HTTPDaemon.class);
        services.start();

        // Register bridges
        bridges = new ServiceManager<TotalFreedomMod>(plugin);
        btb = bridges.registerService(BukkitTelnetBridge.class);
        esb = bridges.registerService(EssentialsBridge.class);
        web = bridges.registerService(WorldEditBridge.class);
        bridges.start();

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
