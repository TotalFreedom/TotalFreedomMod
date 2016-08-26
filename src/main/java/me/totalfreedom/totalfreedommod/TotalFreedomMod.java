package me.totalfreedom.totalfreedommod;

import net.pravian.aero.plugin.*;
import me.totalfreedom.totalfreedommod.config.*;
import net.pravian.aero.component.service.*;
import me.totalfreedom.totalfreedommod.world.*;
import me.totalfreedom.totalfreedommod.admin.*;
import me.totalfreedom.totalfreedommod.rank.*;
import me.totalfreedom.totalfreedommod.command.*;
import me.totalfreedom.totalfreedommod.blocking.command.*;
import me.totalfreedom.totalfreedommod.blocking.*;
import me.totalfreedom.totalfreedommod.player.*;
import me.totalfreedom.totalfreedommod.banning.*;
import me.totalfreedom.totalfreedommod.rollback.*;
import me.totalfreedom.totalfreedommod.caging.*;
import me.totalfreedom.totalfreedommod.freeze.*;
import me.totalfreedom.totalfreedommod.fun.*;
import me.totalfreedom.totalfreedommod.httpd.*;
import me.totalfreedom.totalfreedommod.bridge.*;
import me.totalfreedom.totalfreedommod.util.*;
import org.mcstats.*;
import org.bukkit.plugin.*;
import org.bukkit.scheduler.*;
import org.bukkit.*;
import java.util.*;
import java.io.*;

public class TotalFreedomMod extends AeroPlugin<TotalFreedomMod>
{
    public static final String CONFIG_FILENAME = "config.yml";
    public static final BuildProperties build;
    public static String pluginName;
    public static String pluginVersion;
    public MainConfig config;
    public ServiceManager<TotalFreedomMod> services;
    public ServerInterface si;
    public SavedFlags sf;
    public WorldManager wm;
    public LogViewer lv;
    public AdminList al;
    public RankManager rm;
    public CommandLoader cl;
    public CommandBlocker cb;
    public EventBlocker eb;
    public BlockBlocker bb;
    public MobBlocker mb;
    public InteractBlocker ib;
    public PotionBlocker pb;
    public LoginProcess lp;
    public AntiNuke nu;
    public AntiSpam as;
    public PlayerList pl;
    public Announcer an;
    public ChatManager cm;
    public BanManager bm;
    public PermbanList pm;
    public ProtectArea pa;
    public GameRuleHandler gr;
    public RollbackManager rb;
    public CommandSpy cs;
    public Cager ca;
    public Freezer fm;
    public Orbiter or;
    public Muter mu;
    public Fuckoff fo;
    public AutoKick ak;
    public AutoEject ae;
    public MovementValidator mv;
    public EntityWiper ew;
    public FrontDoor fd;
    public ServerPing sp;
    public ItemFun it;
    public Landminer lm;
    public MP44 mp;
    public Jumppads jp;
    public Trailer tr;
    public HTTPDaemon hd;
    public ServiceManager<TotalFreedomMod> bridges;
    public BukkitTelnetBridge btb;
    public EssentialsBridge esb;
    public LibsDisguisesBridge ldb;
    public WorldEditBridge web;
    
    public void load() {
        TotalFreedomMod.pluginName = ((TotalFreedomMod)this.plugin).getDescription().getName();
        TotalFreedomMod.pluginVersion = ((TotalFreedomMod)this.plugin).getDescription().getVersion();
        FLog.setPluginLogger(((TotalFreedomMod)this.plugin).getLogger());
        FLog.setServerLogger(this.server.getLogger());
        TotalFreedomMod.build.load((TotalFreedomMod)this.plugin);
    }
    
    public void enable() {
        FLog.info("Created by Madgeek1450 and Prozza");
        FLog.info("Version " + TotalFreedomMod.build.formattedVersion());
        FLog.info("Compiled " + TotalFreedomMod.build.date + " by " + TotalFreedomMod.build.author);
        final MethodTimer timer = new MethodTimer();
        timer.start();
        ServerInterface.warnVersion();
        FUtil.deleteCoreDumps();
        FUtil.deleteFolder(new File("./_deleteme"));
        new ConfigConverter((TotalFreedomMod)this.plugin).convert();
        final BackupManager backups = new BackupManager(this);
        backups.createBackups("config.yml", true);
        backups.createBackups("admins.yml");
        backups.createBackups("permbans.yml");
        (this.config = new MainConfig(this)).load();
        this.services = (ServiceManager<TotalFreedomMod>)new ServiceManager(this.plugin);
        this.si = (ServerInterface)this.services.registerService((Class)ServerInterface.class);
        this.sf = (SavedFlags)this.services.registerService((Class)SavedFlags.class);
        this.wm = (WorldManager)this.services.registerService((Class)WorldManager.class);
        this.lv = (LogViewer)this.services.registerService((Class)LogViewer.class);
        this.al = (AdminList)this.services.registerService((Class)AdminList.class);
        this.rm = (RankManager)this.services.registerService((Class)RankManager.class);
        this.cl = (CommandLoader)this.services.registerService((Class)CommandLoader.class);
        this.cb = (CommandBlocker)this.services.registerService((Class)CommandBlocker.class);
        this.eb = (EventBlocker)this.services.registerService((Class)EventBlocker.class);
        this.bb = (BlockBlocker)this.services.registerService((Class)BlockBlocker.class);
        this.mb = (MobBlocker)this.services.registerService((Class)MobBlocker.class);
        this.ib = (InteractBlocker)this.services.registerService((Class)InteractBlocker.class);
        this.pb = (PotionBlocker)this.services.registerService((Class)PotionBlocker.class);
        this.lp = (LoginProcess)this.services.registerService((Class)LoginProcess.class);
        this.nu = (AntiNuke)this.services.registerService((Class)AntiNuke.class);
        this.as = (AntiSpam)this.services.registerService((Class)AntiSpam.class);
        this.pl = (PlayerList)this.services.registerService((Class)PlayerList.class);
        this.an = (Announcer)this.services.registerService((Class)Announcer.class);
        this.cm = (ChatManager)this.services.registerService((Class)ChatManager.class);
        this.bm = (BanManager)this.services.registerService((Class)BanManager.class);
        this.pm = (PermbanList)this.services.registerService((Class)PermbanList.class);
        this.pa = (ProtectArea)this.services.registerService((Class)ProtectArea.class);
        this.gr = (GameRuleHandler)this.services.registerService((Class)GameRuleHandler.class);
        this.rb = (RollbackManager)this.services.registerService((Class)RollbackManager.class);
        this.cs = (CommandSpy)this.services.registerService((Class)CommandSpy.class);
        this.ca = (Cager)this.services.registerService((Class)Cager.class);
        this.fm = (Freezer)this.services.registerService((Class)Freezer.class);
        this.or = (Orbiter)this.services.registerService((Class)Orbiter.class);
        this.mu = (Muter)this.services.registerService((Class)Muter.class);
        this.fo = (Fuckoff)this.services.registerService((Class)Fuckoff.class);
        this.ak = (AutoKick)this.services.registerService((Class)AutoKick.class);
        this.ae = (AutoEject)this.services.registerService((Class)AutoEject.class);
        this.mv = (MovementValidator)this.services.registerService((Class)MovementValidator.class);
        this.ew = (EntityWiper)this.services.registerService((Class)EntityWiper.class);
        this.fd = (FrontDoor)this.services.registerService((Class)FrontDoor.class);
        this.sp = (ServerPing)this.services.registerService((Class)ServerPing.class);
        this.it = (ItemFun)this.services.registerService((Class)ItemFun.class);
        this.lm = (Landminer)this.services.registerService((Class)Landminer.class);
        this.mp = (MP44)this.services.registerService((Class)MP44.class);
        this.jp = (Jumppads)this.services.registerService((Class)Jumppads.class);
        this.tr = (Trailer)this.services.registerService((Class)Trailer.class);
        this.hd = (HTTPDaemon)this.services.registerService((Class)HTTPDaemon.class);
        this.services.start();
        this.bridges = (ServiceManager<TotalFreedomMod>)new ServiceManager(this.plugin);
        this.btb = (BukkitTelnetBridge)this.bridges.registerService((Class)BukkitTelnetBridge.class);
        this.esb = (EssentialsBridge)this.bridges.registerService((Class)EssentialsBridge.class);
		this.ldb = (LibsDisguisesBridge)this.bridges.registerService((Class)LibsDisguisesBridge.class);
        this.web = (WorldEditBridge)this.bridges.registerService((Class)WorldEditBridge.class);
        this.bridges.start();
        timer.update();
        FLog.info("Version " + TotalFreedomMod.pluginVersion + " for " + "v1_10_R1" + " enabled in " + timer.getTotal() + "ms");
        try {
            final Metrics metrics = new Metrics((Plugin)this.plugin);
            metrics.start();
        }
        catch (IOException ex) {
            FLog.warning("Failed to submit metrics data: " + ex.getMessage());
        }
        new BukkitRunnable() {
            public void run() {
                ((TotalFreedomMod)TotalFreedomMod.this.plugin).pa.autoAddSpawnpoints();
            }
        }.runTaskLater((Plugin)this.plugin, 60L);
    }
    
    public void disable() {
        this.bridges.stop();
        this.services.stop();
        this.server.getScheduler().cancelTasks((Plugin)this.plugin);
        FLog.info("Plugin disabled");
    }
    
    public static TotalFreedomMod plugin() {
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(TotalFreedomMod.pluginName)) {
                return (TotalFreedomMod)plugin;
            }
        }
        return null;
    }
    
    static {
        build = new BuildProperties();
    }
    
    public static class BuildProperties
    {
        public String author;
        public String codename;
        public String version;
        public String number;
        public String date;
        public String head;
        
        public void load(final TotalFreedomMod plugin) {
            try {
                Properties props;
                try (final InputStream in = plugin.getResource("build.properties")) {
                    props = new Properties();
                    props.load(in);
                }
                this.author = props.getProperty("program.build.author", "unknown");
                this.codename = props.getProperty("program.build.codename", "unknown");
                this.version = props.getProperty("program.build.version", "unknown");
                this.number = props.getProperty("program.build.number", "1");
                this.date = props.getProperty("program.build.date", "unknown");
                this.head = props.getProperty("program.build.head", "unknown");
            }
            catch (Exception ex) {
                FLog.severe("Could not load build properties! Did you compile with Netbeans/ANT?");
                FLog.severe(ex);
            }
        }
        
        public String formattedVersion() {
            return TotalFreedomMod.pluginVersion + "." + this.number + " (" + this.head + ")";
        }
    }
}
