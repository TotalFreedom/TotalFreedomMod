package me.StevenLawson.TotalFreedomMod.World;

import me.StevenLawson.TotalFreedomMod.World.TFM_CustomWorld;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_GameRuleHandler;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public final class TFM_AdminWorld extends TFM_CustomWorld
{
    private static final long CACHE_CLEAR_FREQUENCY = 30L * 1000L; //30 seconds, milliseconds
    private static final long TP_COOLDOWN_TIME = 500L; //0.5 seconds, milliseconds
    private static final String GENERATION_PARAMETERS = TFM_ConfigEntry.FLATLANDS_GENERATE_PARAMS.getString();
    private static final String WORLD_NAME = "adminworld";
    //
    private final Map<Player, Long> teleportCooldown = new HashMap<Player, Long>();
    private final Map<CommandSender, Boolean> accessCache = new HashMap<CommandSender, Boolean>();
    //
    private Long cacheLastCleared = null;
    private Map<Player, Player> guestList = new HashMap<Player, Player>(); // Guest, Supervisor
    private WeatherMode weatherMode = WeatherMode.OFF;
    private TimeOfDay timeOfDay = TimeOfDay.INHERIT;

    private TFM_AdminWorld()
    {
    }

    @Override
    public void sendToWorld(Player player)
    {
        if (!canAccessWorld(player))
        {
            return;
        }

        super.sendToWorld(player);
    }

    @Override
    protected World generateWorld()
    {
        WorldCreator worldCreator = new WorldCreator(WORLD_NAME);
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generator(new CleanroomChunkGenerator(GENERATION_PARAMETERS));

        World world = Bukkit.getServer().createWorld(worldCreator);

        world.setSpawnFlags(false, false);
        world.setSpawnLocation(0, 50, 0);

        Block welcomeSignBlock = world.getBlockAt(0, 50, 0);
        welcomeSignBlock.setType(Material.SIGN_POST);
        org.bukkit.block.Sign welcomeSign = (org.bukkit.block.Sign) welcomeSignBlock.getState();

        org.bukkit.material.Sign signData = (org.bukkit.material.Sign) welcomeSign.getData();
        signData.setFacingDirection(BlockFace.NORTH);

        welcomeSign.setLine(0, ChatColor.GREEN + "AdminWorld");
        welcomeSign.setLine(1, ChatColor.DARK_GRAY + "---");
        welcomeSign.setLine(2, ChatColor.YELLOW + "Spawn Point");
        welcomeSign.setLine(3, ChatColor.DARK_GRAY + "---");
        welcomeSign.update();

        TFM_GameRuleHandler.commitGameRules();

        return world;
    }

    public boolean addGuest(Player guest, Player supervisor)
    {
        if (guest == supervisor || TFM_AdminList.isSuperAdmin(guest))
        {
            return false;
        }

        if (TFM_AdminList.isSuperAdmin(supervisor))
        {
            guestList.put(guest, supervisor);
            wipeAccessCache();
            return true;
        }

        return false;
    }

    public Player removeGuest(Player guest)
    {
        final Player player = guestList.remove(guest);
        wipeAccessCache();
        return player;
    }

    public Player removeGuest(String partialName)
    {
        partialName = partialName.toLowerCase();
        final Iterator<Player> it = guestList.keySet().iterator();

        while (it.hasNext())
        {
            final Player player = it.next();
            if (player.getName().toLowerCase().contains(partialName))
            {
                removeGuest(player);
                return player;
            }
        }

        return null;
    }

    public String guestListToString()
    {
        final List<String> output = new ArrayList<String>();
        final Iterator<Map.Entry<Player, Player>> it = guestList.entrySet().iterator();
        while (it.hasNext())
        {
            final Entry<Player, Player> entry = it.next();
            final Player player = entry.getKey();
            final Player supervisor = entry.getValue();
            output.add(player.getName() + " (Supervisor: " + supervisor.getName() + ")");
        }
        return StringUtils.join(output, ", ");
    }

    public void purgeGuestList()
    {
        guestList.clear();
        wipeAccessCache();
    }

    public boolean validateMovement(PlayerMoveEvent event)
    {
        World world;
        try
        {
            world = getWorld();
        }
        catch (Exception ex)
        {
            return true;
        }

        if (world != null && event.getTo().getWorld() == world)
        {
            final Player player = event.getPlayer();
            if (!canAccessWorld(player))
            {
                Long lastTP = teleportCooldown.get(player);
                long currentTimeMillis = System.currentTimeMillis();
                if (lastTP == null || lastTP.longValue() + TP_COOLDOWN_TIME <= currentTimeMillis)
                {
                    teleportCooldown.put(player, currentTimeMillis);
                    TFM_Log.info(player.getName() + " attempted to access the AdminWorld.");
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        }
                    }.runTaskLater(TotalFreedomMod.plugin, 1L);
                }
                event.setCancelled(true);
                return false;
            }
        }

        return true;
    }

    public void wipeAccessCache()
    {
        cacheLastCleared = System.currentTimeMillis();
        accessCache.clear();
    }

    public boolean canAccessWorld(final Player player)
    {
        long currentTimeMillis = System.currentTimeMillis();
        if (cacheLastCleared == null || cacheLastCleared.longValue() + CACHE_CLEAR_FREQUENCY <= currentTimeMillis)
        {
            cacheLastCleared = currentTimeMillis;
            accessCache.clear();
        }

        Boolean cached = accessCache.get(player);
        if (cached == null)
        {
            boolean canAccess = TFM_AdminList.isSuperAdmin(player);
            if (!canAccess)
            {
                Player supervisor = guestList.get(player);
                canAccess = supervisor != null && supervisor.isOnline() && TFM_AdminList.isSuperAdmin(supervisor);
                if (!canAccess)
                {
                    guestList.remove(player);
                }
            }
            cached = canAccess;
            accessCache.put(player, cached);
        }
        return cached;
    }

    public static enum WeatherMode
    {
        OFF("off"),
        RAIN("rain"),
        STORM("storm,thunderstorm");
        //
        private final List<String> aliases;

        private WeatherMode(String aliases)
        {
            this.aliases = Arrays.asList(StringUtils.split(aliases, ","));
        }

        private void setWorldToWeather(World world)
        {
            world.setStorm(this == RAIN || this == STORM);
            world.setWeatherDuration(this == RAIN || this == STORM ? 20 * 60 * 5 : 0);

            world.setThundering(this == STORM);
            world.setThunderDuration(this == STORM ? 20 * 60 * 5 : 0);
        }

        public static WeatherMode getByAlias(String needle)
        {
            needle = needle.toLowerCase();
            for (WeatherMode mode : values())
            {
                if (mode.aliases.contains(needle))
                {
                    return mode;
                }
            }
            return null;
        }
    }

    public static enum TimeOfDay
    {
        INHERIT(),
        SUNRISE("sunrise,morning", 0),
        NOON("noon,midday,day", 6000),
        SUNSET("sunset,evening", 12000),
        MIDNIGHT("midnight,night", 18000);
        //
        private final int timeTicks;
        private final List<String> aliases;

        private TimeOfDay()
        {
            this.timeTicks = 0;
            this.aliases = null;
        }

        private TimeOfDay(String aliases, int timeTicks)
        {
            this.timeTicks = timeTicks;
            this.aliases = Arrays.asList(StringUtils.split(aliases, ","));
        }

        public int getTimeTicks()
        {
            return timeTicks;
        }

        public void setWorldToTime(World world)
        {
            long time = world.getTime();
            time -= time % 24000;
            world.setTime(time + 24000 + getTimeTicks());
        }

        public static TimeOfDay getByAlias(String needle)
        {
            needle = needle.toLowerCase();
            for (TimeOfDay time : values())
            {
                if (time.aliases != null && time.aliases.contains(needle))
                {
                    return time;
                }
            }
            return null;
        }
    }

    public WeatherMode getWeatherMode()
    {
        return weatherMode;
    }

    public void setWeatherMode(final WeatherMode weatherMode)
    {
        this.weatherMode = weatherMode;

        try
        {
            weatherMode.setWorldToWeather(getWorld());
        }
        catch (Exception ex)
        {
        }
    }

    public TimeOfDay getTimeOfDay()
    {
        return timeOfDay;
    }

    public void setTimeOfDay(final TimeOfDay timeOfDay)
    {
        this.timeOfDay = timeOfDay;

        try
        {
            timeOfDay.setWorldToTime(getWorld());
        }
        catch (Exception ex)
        {
        }
    }

    public static TFM_AdminWorld getInstance()
    {
        return TFM_AdminWorldHolder.INSTANCE;
    }

    private static class TFM_AdminWorldHolder
    {
        private static final TFM_AdminWorld INSTANCE = new TFM_AdminWorld();
    }
}
