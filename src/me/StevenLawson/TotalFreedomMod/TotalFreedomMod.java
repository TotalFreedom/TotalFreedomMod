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
    public int nukeMonitorCountBreak = 40;
    public double nukeMonitorRange = 10.0D;
    public Boolean preprocessLogEnabled = false;
    public int freecamTriggerCount = 10;
    
    public static final long HEARTBEAT_RATE = 5L; //Seconds
    
    public static final String MSG_NO_PERMS = ChatColor.YELLOW + "You do not have permission to use this command.";
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String CAKE_LYRICS = "But there's no sense crying over every mistake. You just keep on trying till you run out of cake.";
    
    public boolean allPlayersFrozen = false;
    public static Map<Player, TFM_UserInfo> userinfo = new HashMap<Player, TFM_UserInfo>();
    
    private TFM_Cmds_OP OPCommands = new TFM_Cmds_OP(this);
    private TFM_Cmds_Override OverrideCommands = new TFM_Cmds_Override(this);
    private TFM_Cmds_General GeneralCommands = new TFM_Cmds_General(this);
    private TFM_Cmds_AntiBlock AntiblockCommands = new TFM_Cmds_AntiBlock(this);
    private TFM_Cmds_Admin AdminCommands = new TFM_Cmds_Admin(this);

    @Override
    public void onEnable()
    {
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
            TFM_UserInfo playerdata = TotalFreedomMod.userinfo.get(p);
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
                if (ent instanceof Arrow || (ent instanceof TNTPrimed && !this.allowExplosions) || ent instanceof Item || ent instanceof ExperienceOrb)
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
        createDefaultConfiguration("config.yml");
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        allowExplosions = config.getBoolean("allow_explosions", false);
        allowFirePlace = config.getBoolean("allow_fire_place", false);
        allowFireSpread = config.getBoolean("allow_fire_spread", false);
        allowLavaDamage = config.getBoolean("allow_lava_damage", false);
        allowLavaPlace = config.getBoolean("allow_lava_place", false);
        allowWaterPlace = config.getBoolean("allow_water_place", false);
        autoEntityWipe = config.getBoolean("auto_wipe", true);
        explosiveRadius = config.getDouble("explosiveRadius", 4.0D);
        nukeMonitor = config.getBoolean("nuke_monitor", true);
        nukeMonitorCountBreak = config.getInt("nuke_monitor_count", 100);
        nukeMonitorRange = config.getDouble("nuke_monitor_range", 10.0D);
        preprocessLogEnabled = config.getBoolean("preprocess_log", true);
        freecamTriggerCount = config.getInt("freecam_trigger_count", 10);

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
        }
    }

    private void createDefaultConfiguration(String name)
    {
        File actual = new File(getDataFolder(), name);
        if (!actual.exists())
        {
            InputStream input = null;
            try
            {
                JarFile file = new JarFile(getFile());
                ZipEntry copy = file.getEntry("src/" + name);
                if (copy == null)
                {
                    return;
                }
                input = file.getInputStream(copy);
            }
            catch (IOException ioex)
            {
                log.severe("[TotalFreedomMod]: Unable to read default configuration: " + name);
            }
            if (input != null)
            {
                FileOutputStream output = null;

                try
                {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;
                    while ((length = input.read(buf)) > 0)
                    {
                        output.write(buf, 0, length);
                    }

                    log.info("[TotalFreedomMod]: Default configuration file written: " + name);
                }
                catch (IOException ioex)
                {
                    log.severe("[TotalFreedomMod]: Unable to write default configuration: " + name);
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

    public void gotoWorld(CommandSender sender, String targetworld)
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

    public void buildHistory(Location location, int length, TFM_UserInfo playerdata)
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

    public void generateCube(Location location, int length, Material material)
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
}
