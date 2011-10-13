package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TotalFreedomMod extends JavaPlugin
{
    public TotalFreedomMod tfm = this;
    
    private final TFM_EntityListener entityListener = new TFM_EntityListener(this);
    private final TFM_BlockListener blockListener = new TFM_BlockListener(this);
    private final TFM_PlayerListener playerListener = new TFM_PlayerListener(this);
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    public boolean allPlayersFrozen = false;
    public static Map<Player, TFM_UserInfo> userinfo = new HashMap<Player, TFM_UserInfo>();
    
    public static final long HEARTBEAT_RATE = 5L; //Seconds
    public static final String CONFIG_FILE = "config.yml";
    public static final String MSG_NO_PERMS = ChatColor.YELLOW + "You do not have permission to use this command.";
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String CAKE_LYRICS = "But there's no sense crying over every mistake. You just keep on trying till you run out of cake.";

    @Override
    public void onEnable()
    {
        loadTFMConfig();
        registerEventHandlers();
        registerCommands();
        
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new TFM_Heartbeat(this), HEARTBEAT_RATE * 20L, HEARTBEAT_RATE * 20L);

        log.log(Level.INFO, "[" + getDescription().getName() + "] - Enabled! - Version: " + getDescription().getVersion() + " by Madgeek1450");
    }

    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
        log.log(Level.INFO, "[" + getDescription().getName() + "] - Disabled.");
    }
    
    class TFM_Heartbeat implements Runnable
    {
        private TotalFreedomMod plugin;

        TFM_Heartbeat(TotalFreedomMod instance)
        {
            this.plugin = instance;
        }

        @Override
        public void run()
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                TFM_UserInfo playerdata = TotalFreedomMod.userinfo.get(p);
                if (playerdata != null)
                {
                    playerdata.resetMsgCount();
                    playerdata.resetBlockDestroyCount();
                }
            }

            if (plugin.autoEntityWipe)
            {
                plugin.wipeDropEntities();
            }
            
            if (plugin.disableNight)
            {
                for (World world : Bukkit.getWorlds())
                {
                    if (world.getTime() > 12000L)
                    {
                        TotalFreedomMod.setWorldTime(world, 1000L);
                    }
                }
            }
        }
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

    public int wipeDropEntities()
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity ent : world.getEntities())
            {
                if (ent instanceof Arrow || (ent instanceof TNTPrimed && !this.allowExplosions) || ent instanceof Item || ent instanceof ExperienceOrb)
                {
                    ent.remove();
                    removed++;
                }
            }
        }
        return removed;
    }
    
    public static void tfm_broadcastMessage(String message, ChatColor color)
    {
        log.info(message);

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(color + message);
        }
    }

    public static void tfm_broadcastMessage(String message)
    {
        log.info(ChatColor.stripColor(message));

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(message);
        }
    }

    public static String implodeStringList(String glue, List<String> pieces)
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

    public static String formatLocation(Location in_loc)
    {
        return String.format("%s: (%d, %d, %d)",
                in_loc.getWorld().getName(),
                Math.round(in_loc.getX()),
                Math.round(in_loc.getY()),
                Math.round(in_loc.getZ()));
    }

    public static void gotoWorld(CommandSender sender, String targetworld)
    {
        if (sender instanceof Player)
        {
            Player sender_p = (Player) sender;

            if (sender_p.getWorld().getName().equalsIgnoreCase(targetworld))
            {
                sender.sendMessage(ChatColor.GRAY + "Going to main world.");
                Bukkit.getServer().dispatchCommand(sender, "world 0");
                return;
            }

            for (World world : Bukkit.getWorlds())
            {
                if (world.getName().equalsIgnoreCase(targetworld))
                {
                    sender.sendMessage(ChatColor.GRAY + "Going to world: " + targetworld);
                    Bukkit.getServer().dispatchCommand(sender, "mv tp " + targetworld);
                    return;
                }
            }
        }
        else
        {
            sender.sendMessage("This command may not be used from the console.");
        }
    }

    public static void buildHistory(Location location, int length, TFM_UserInfo playerdata)
    {
        Block center_block = location.getBlock();
        for (int x_offset = -length; x_offset <= length; x_offset++)
        {
            for (int y_offset = -length; y_offset <= length; y_offset++)
            {
                for (int z_offset = -length; z_offset <= length; z_offset++)
                {
                    Block block = center_block.getRelative(x_offset, y_offset, z_offset);
                    playerdata.insertHistoryBlock(block.getLocation(), block.getType());
                }
            }
        }
    }

    public static void generateCube(Location location, int length, Material material)
    {
        Block center_block = location.getBlock();
        for (int x_offset = -length; x_offset <= length; x_offset++)
        {
            for (int y_offset = -length; y_offset <= length; y_offset++)
            {
                for (int z_offset = -length; z_offset <= length; z_offset++)
                {
                    center_block.getRelative(x_offset, y_offset, z_offset).setType(material);
                }
            }
        }
    }
    
    public static void setWorldTime(World world, long ticks)
    {
        long time = world.getTime();
        time -= time % 24000;
        world.setTime(time + 24000 + ticks);
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
    public double nukeMonitorRange = 10.0D;
    public int freecamTriggerCount = 10;
    public Boolean preprocessLogEnabled = true;
    public Boolean disableNight = true;
    public List<String> superadmins = new ArrayList<String>();
    public List<String> superadmin_ips = new ArrayList<String>();

    private void loadTFMConfig()
    {
        createDefaultConfiguration(CONFIG_FILE);

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
        nukeMonitorCountBreak = config.getInt("nuke_monitor_count", nukeMonitorCountBreak);
        nukeMonitorRange = config.getDouble("nuke_monitor_range", nukeMonitorRange);
        freecamTriggerCount = config.getInt("freecam_trigger_count", freecamTriggerCount);
        preprocessLogEnabled = config.getBoolean("preprocess_log", preprocessLogEnabled);
        disableNight = config.getBoolean("disable_night", disableNight);

        superadmins = (List<String>) config.getList("superadmins", null);
        if (superadmins == null)
        {
            superadmins = new ArrayList<String>();
            superadmins.add("Madgeek1450");
            superadmins.add("markbyron");
        }

        superadmin_ips = (List<String>) config.getList("superadmin_ips", null);
        if (superadmin_ips == null)
        {
            superadmin_ips = new ArrayList<String>();
            superadmin_ips.add("127.0.0.1");
        }
    }

    private void createDefaultConfiguration(String name)
    {
        File actual = new File(getDataFolder(), name);
        if (!actual.exists())
        {
            log.info("[" + getDescription().getName() + "]: Installing default configuration file template: " + actual.getPath());
            InputStream input = null;
            try
            {
                JarFile file = new JarFile(getFile());
                ZipEntry copy = file.getEntry(name);
                if (copy == null)
                {
                    log.severe("[" + getDescription().getName() + "]: Unable to read default configuration: " + actual.getPath());
                    return;
                }
                input = file.getInputStream(copy);
            }
            catch (IOException ioex)
            {
                log.severe("[" + getDescription().getName() + "]: Unable to read default configuration: " + actual.getPath());
            }
            if (input != null)
            {
                FileOutputStream output = null;

                try
                {
                    getDataFolder().mkdirs();
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;
                    while ((length = input.read(buf)) > 0)
                    {
                        output.write(buf, 0, length);
                    }

                    log.info("[" + getDescription().getName() + "]: Default configuration file written: " + actual.getPath());
                }
                catch (IOException ioex)
                {
                    log.log(Level.SEVERE, "[" + getDescription().getName() + "]: Unable to write default configuration: " + actual.getPath(), ioex);
                }
                finally
                {
                    try
                    {
                        if (input != null)
                        {
                            input.close();
                        }
                    }
                    catch (IOException ioex)
                    {
                    }

                    try
                    {
                        if (output != null)
                        {
                            output.close();
                        }
                    }
                    catch (IOException ioex)
                    {
                    }
                }
            }
        }
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

    private TFM_Cmds_OP OPCommands = new TFM_Cmds_OP(this);
    private TFM_Cmds_Override OverrideCommands = new TFM_Cmds_Override(this);
    private TFM_Cmds_General GeneralCommands = new TFM_Cmds_General(this);
    private TFM_Cmds_AntiBlock AntiblockCommands = new TFM_Cmds_AntiBlock(this);
    private TFM_Cmds_Admin AdminCommands = new TFM_Cmds_Admin(this);
    
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
        this.getCommand("nether").setExecutor(GeneralCommands);
        this.getCommand("banlist").setExecutor(GeneralCommands);
        this.getCommand("ipbanlist").setExecutor(GeneralCommands);

        this.getCommand("fr").setExecutor(AdminCommands);
        this.getCommand("gtfo").setExecutor(AdminCommands);
        this.getCommand("gadmin").setExecutor(AdminCommands);
        this.getCommand("wildcard").setExecutor(AdminCommands);
        this.getCommand("nonuke").setExecutor(AdminCommands);
        this.getCommand("prelog").setExecutor(AdminCommands);
        this.getCommand("cake").setExecutor(AdminCommands);
        this.getCommand("gcmd").setExecutor(AdminCommands);
        this.getCommand("qjail").setExecutor(AdminCommands);
        this.getCommand("umd").setExecutor(AdminCommands);
        this.getCommand("csay").setExecutor(AdminCommands);
        this.getCommand("cage").setExecutor(AdminCommands);
        this.getCommand("orbit").setExecutor(AdminCommands);

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
