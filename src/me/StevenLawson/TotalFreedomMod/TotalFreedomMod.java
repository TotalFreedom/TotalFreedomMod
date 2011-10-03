package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class TotalFreedomMod extends JavaPlugin
{
    public TotalFreedomMod tfm = this;
    
    private final TFM_EntityListener entityListener = new TFM_EntityListener(this);
    private final TFM_BlockListener blockListener = new TFM_BlockListener(this);
    private final TFM_PlayerListener playerListener = new TFM_PlayerListener(this);
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    protected static Configuration CONFIG;
    public List<String> superadmins = new ArrayList<String>();
    public List<String> superadmin_ips = new ArrayList<String>();
    public Boolean allowExplosions = false;
    public boolean allowFirePlace = false;
    public Boolean allowFireSpread = false;
    public Boolean allowLavaDamage = false;
    public boolean allowLavaPlace = false;
    public boolean allowWaterPlace = false;
    public boolean autoEntityWipe = false;
    public double explosiveRadius = 4.0D;
    public boolean nukeMonitor = true;
    public int nukeMonitorCount = 40;
    public double nukeMonitorRange = 10.0D;
    public Boolean preprocessLogEnabled = false;
    
    public boolean allPlayersFrozen = false;
    public HashMap userinfo = new HashMap();
    
    private static final long HEARTBEAT_RATE = 5L; //Seconds
    
    public static final String MSG_NO_PERMS = ChatColor.YELLOW + "You do not have permission to use this command.";
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String CAKE_LYRICS = "But there's no sense crying over every mistake. You just keep on trying till you run out of cake.";
    
    private TFM_Cmds_OP OPCommands = new TFM_Cmds_OP(this);
    private TFM_Cmds_Override OverrideCommands = new TFM_Cmds_Override(this);
    private TFM_Cmds_General GeneralCommands = new TFM_Cmds_General(this);
    private TFM_Cmds_AntiBlock AntiblockCommands = new TFM_Cmds_AntiBlock(this);
    private TFM_Cmds_Admin AdminCommands = new TFM_Cmds_Admin(this);

    @Override
    public void onEnable()
    {
        CONFIG = getConfiguration();
        loadConfig();

        registerEventHandlers();

        registerCommands();

        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
        {
            @Override
            public void run()
            {
                tfm.tfm_Heartbeat();
            }
        }, HEARTBEAT_RATE * 20L, HEARTBEAT_RATE * 20L);

        log.log(Level.INFO, "[Total Freedom Mod] - Enabled! - Version: " + this.getDescription().getVersion() + " by Madgeek1450");
        log.log(Level.INFO, "[Total Freedom Mod] - Loaded superadmin names: " + implodeStringList(", ", superadmins));
        log.log(Level.INFO, "[Total Freedom Mod] - Loaded superadmin IPs: " + implodeStringList(", ", superadmin_ips));
        log.log(Level.INFO, "[Total Freedom Mod] - Auto drop deleter is " + (autoEntityWipe ? "enabled" : "disabled") + ".");
    }

    @Override
    public void onDisable()
    {
        log.log(Level.INFO, "[Total Freedom Mod] - Disabled.");
    }

    public void tfm_broadcastMessage(String message, ChatColor color)
    {
        log.info(message);

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(color + message);
        }
    }

    public void tfm_broadcastMessage(String message)
    {
        log.info(ChatColor.stripColor(message));

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(message);
        }
    }

    public String implodeStringList(String glue, List<String> pieces)
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

    public String formatLocation(Location in_loc)
    {
        return String.format("%s: (%d, %d, %d)",
                in_loc.getWorld().getName(),
                Math.round(in_loc.getX()),
                Math.round(in_loc.getY()),
                Math.round(in_loc.getZ()));
    }

    public boolean isUserSuperadmin(CommandSender user)
    {
        try
        {
            if (!(user instanceof Player))
            {
                return true;
            }

            if (Bukkit.getOnlineMode())
            {
                if (superadmins.contains(user.getName()))
                {
                    return true;
                }
            }

            Player p = (Player) user;
            if (p != null)
            {
                InetSocketAddress ip_address_obj = p.getAddress();
                if (ip_address_obj != null)
                {
                    String user_ip = ip_address_obj.getAddress().toString().replaceAll("/", "").trim();
                    if (user_ip != null && !user_ip.isEmpty())
                    {
                        if (superadmin_ips.contains(user_ip))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            log.severe("Exception in TotalFreedomMod.isUserSuperadmin: " + ex.getMessage());
        }

        return false;
    }

    private void tfm_Heartbeat()
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            TFM_UserInfo playerdata = (TFM_UserInfo) this.userinfo.get(p);
            if (playerdata != null)
            {
                playerdata.resetMsgCount();
                playerdata.resetBlockDestroyCount();
            }
        }

        if (this.autoEntityWipe)
        {
            wipeDropEntities();
        }
    }

    public int wipeDropEntities()
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity ent : world.getEntities())
            {
                if (ent instanceof Arrow || ent instanceof TNTPrimed || ent instanceof Item || ent instanceof ExperienceOrb)
                {
                    ent.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

    private void loadConfig()
    {
        File configfile = new File("plugins/TotalFreedomMod/config.yml");
        if (!configfile.exists())
        {
            log.log(Level.INFO, "[Total Freedom Mod] - Generating default config file (plugins/TotalFreedomMod/config.yml)...");
            CONFIG.setProperty("superadmins", new String[]
                    {
                        "Madgeek1450", "markbyron"
                    });
            CONFIG.setProperty("superadmin_ips", new String[]
                    {
                        "0.0.0.0"
                    });
            CONFIG.setProperty("allow_explosions", false);
            CONFIG.setProperty("allow_fire_place", false);
            CONFIG.setProperty("allow_fire_spread", false);
            CONFIG.setProperty("allow_lava_damage", false);
            CONFIG.setProperty("allow_lava_place", false);
            CONFIG.setProperty("allow_water_place", false);
            CONFIG.setProperty("auto_wipe", false);
            CONFIG.setProperty("explosiveRadius", 4.0D);
            CONFIG.setProperty("nuke_monitor", true);
            CONFIG.setProperty("nuke_monitor_count", 40);
            CONFIG.setProperty("nuke_monitor_range", 10.0D);
            CONFIG.setProperty("preprocess_log", false);
            CONFIG.save();
        }
        CONFIG.load();
        superadmins = CONFIG.getStringList("superadmins", null);
        superadmin_ips = CONFIG.getStringList("superadmin_ips", null);
        allowExplosions = CONFIG.getBoolean("allow_explosions", false);
        allowFirePlace = CONFIG.getBoolean("allow_fire_place", false);
        allowFireSpread = CONFIG.getBoolean("allow_fire_spread", false);
        allowLavaDamage = CONFIG.getBoolean("allow_lava_damage", false);
        allowLavaPlace = CONFIG.getBoolean("allow_lava_place", false);
        allowWaterPlace = CONFIG.getBoolean("allow_water_place", false);
        autoEntityWipe = CONFIG.getBoolean("auto_wipe", false);
        explosiveRadius = CONFIG.getDouble("explosiveRadius", 4.0D);
        nukeMonitor = CONFIG.getBoolean("nuke_monitor", true);
        nukeMonitorCount = CONFIG.getInt("nuke_monitor_count", 40);
        nukeMonitorRange = CONFIG.getDouble("nuke_monitor_range", 10.0D);
        preprocessLogEnabled = CONFIG.getBoolean("preprocess_log", false);
    }

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
    }

    private void registerCommands()
    {
        this.getCommand("opme").setExecutor(OPCommands);
        this.getCommand("opall").setExecutor(OPCommands);
        this.getCommand("deopall").setExecutor(OPCommands);
        this.getCommand("qop").setExecutor(OPCommands);
        this.getCommand("qdeop").setExecutor(OPCommands);

        this.getCommand("creative").setExecutor(GeneralCommands);
        this.getCommand("survival").setExecutor(GeneralCommands);
        this.getCommand("status").setExecutor(GeneralCommands);
        this.getCommand("radar").setExecutor(GeneralCommands);
        this.getCommand("mp").setExecutor(GeneralCommands);
        this.getCommand("rd").setExecutor(GeneralCommands);
        this.getCommand("flatlands").setExecutor(GeneralCommands);
        this.getCommand("skylands").setExecutor(GeneralCommands);

        this.getCommand("fr").setExecutor(AdminCommands);
        this.getCommand("gtfo").setExecutor(AdminCommands);
        this.getCommand("gadmin").setExecutor(AdminCommands);
        this.getCommand("wildcard").setExecutor(AdminCommands);
        this.getCommand("nonuke").setExecutor(AdminCommands);
        this.getCommand("prelog").setExecutor(AdminCommands);
        this.getCommand("cake").setExecutor(AdminCommands);

        this.getCommand("explosives").setExecutor(AntiblockCommands);
        this.getCommand("lavadmg").setExecutor(AntiblockCommands);
        this.getCommand("lavaplace").setExecutor(AntiblockCommands);
        this.getCommand("firespread").setExecutor(AntiblockCommands);
        this.getCommand("fireplace").setExecutor(AntiblockCommands);
        this.getCommand("waterplace").setExecutor(AntiblockCommands);

        this.getCommand("say").setExecutor(OverrideCommands);
        this.getCommand("stop").setExecutor(OverrideCommands);
        this.getCommand("list").setExecutor(OverrideCommands);
        this.getCommand("listreal").setExecutor(OverrideCommands);
    }
}
