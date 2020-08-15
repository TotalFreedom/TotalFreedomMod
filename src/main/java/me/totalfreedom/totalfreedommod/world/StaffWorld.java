package me.totalfreedom.totalfreedommod.world;

import java.util.HashMap;
import java.util.Map;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public final class StaffWorld extends CustomWorld
{

    private static final long CACHE_CLEAR_FREQUENCY = 30L * 1000L; //30 seconds, milliseconds
    private static final long TP_COOLDOWN_TIME = 500L; //0.5 seconds, milliseconds
    private static final String GENERATION_PARAMETERS = ConfigEntry.FLATLANDS_GENERATE_PARAMS.getString();
    //
    private final Map<Player, Long> teleportCooldown = new HashMap<>();
    //
    private WorldWeather weather = WorldWeather.OFF;
    private WorldTime time = WorldTime.INHERIT;

    public StaffWorld()
    {
        super("staffworld");
    }

    @Override
    public void sendToWorld(Player player)
    {
        super.sendToWorld(player);
    }

    @Override
    protected World generateWorld()
    {
        final WorldCreator worldCreator = new WorldCreator(getName());
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generator(new CleanroomChunkGenerator(GENERATION_PARAMETERS));

        final World world = Bukkit.getServer().createWorld(worldCreator);

        world.setSpawnFlags(false, false);
        world.setSpawnLocation(0, 50, 0);

        final Block welcomeSignBlock = world.getBlockAt(0, 50, 0);
        welcomeSignBlock.setType(Material.OAK_SIGN);
        org.bukkit.block.Sign welcomeSign = (org.bukkit.block.Sign)welcomeSignBlock.getState();

        org.bukkit.material.Sign signData = (org.bukkit.material.Sign)welcomeSign.getData();
        signData.setFacingDirection(BlockFace.NORTH);

        welcomeSign.setLine(0, ChatColor.GREEN + "StaffWorld");
        welcomeSign.setLine(1, ChatColor.DARK_GRAY + "---");
        welcomeSign.setLine(2, ChatColor.YELLOW + "Spawn Point");
        welcomeSign.setLine(3, ChatColor.DARK_GRAY + "---");
        welcomeSign.update();

        plugin.gr.commitGameRules();
        return world;
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

        if (world == null || !event.getTo().getWorld().equals(world))
        {
            return true;
        }

        final Player player = event.getPlayer();

        Long lastTP = teleportCooldown.get(player);

        long currentTimeMillis = System.currentTimeMillis();
        if (lastTP == null || lastTP + TP_COOLDOWN_TIME <= currentTimeMillis)
        {
            teleportCooldown.put(player, currentTimeMillis);
            FLog.info(player.getName() + " attempted to access the StaffWorld.");
            event.setTo(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        return false;
    }

    public WorldWeather getWeatherMode()
    {
        return weather;
    }

    public void setWeatherMode(final WorldWeather weatherMode)
    {
        this.weather = weatherMode;

        try
        {
            weatherMode.setWorldToWeather(getWorld());
        }
        catch (Exception ex)
        {
        }
    }

    public WorldTime getTimeOfDay()
    {
        return time;
    }

    public void setTimeOfDay(final WorldTime timeOfDay)
    {
        this.time = timeOfDay;

        try
        {
            timeOfDay.setWorldToTime(getWorld());
        }
        catch (Exception ex)
        {
        }
    }

}
