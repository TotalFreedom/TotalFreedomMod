package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FUtil;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import static me.totalfreedom.totalfreedommod.TotalFreedomMod.HEARTBEAT_RATE;
import me.totalfreedom.totalfreedommod.world.WorldWeather;
import net.pravian.aero.component.service.AbstractService;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Heartbeat extends AbstractService<TotalFreedomMod> implements Runnable
{
    private long autoKickTime;
    private BukkitTask task;
    @Getter
    private Long lastRan = null;

    public Heartbeat(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    public void onStart()
    {
        autoKickTime = (long) ConfigEntry.AUTOKICK_TIME.getInteger() * 1000L;

        task = server.getScheduler().runTaskTimer(plugin, this, HEARTBEAT_RATE * 20L, HEARTBEAT_RATE * 20L);
    }

    @Override
    protected void onStop()
    {
        server.getScheduler().cancelTask(task.getTaskId());
        task = null;
    }

    @Override
    public void run()
    {
        lastRan = System.currentTimeMillis();

        final boolean doAwayKickCheck = ConfigEntry.AUTOKICK_ENABLED.getBoolean()
                && plugin.esb.isEssentialsEnabled()
                && ((server.getOnlinePlayers().size() / server.getMaxPlayers()) > ConfigEntry.AUTOKICK_THRESHOLD.getDouble());

        for (Player player : server.getOnlinePlayers())
        {
            final FPlayer playerdata = plugin.pl.getPlayer(player);
            playerdata.resetMsgCount();
            playerdata.resetBlockDestroyCount();
            playerdata.resetBlockPlaceCount();

            if (doAwayKickCheck)
            {
                final long lastActivity = plugin.esb.getLastActivity(player.getName());
                if (lastActivity > 0 && lastActivity + autoKickTime < System.currentTimeMillis())
                {
                    player.kickPlayer("Automatically kicked by server for inactivity.");
                }
            }
        }

        if (ConfigEntry.AUTO_ENTITY_WIPE.getBoolean())
        {
            FUtil.TFM_EntityWiper.wipeEntities(!ConfigEntry.ALLOW_EXPLOSIONS.getBoolean(), false);
        }

        if (ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            for (World world : server.getWorlds())
            {
                try
                {
                    if (world == plugin.wm.adminworld.getWorld() && plugin.wm.adminworld.getWeatherMode() != WorldWeather.OFF)
                    {
                        continue;
                    }
                }
                catch (Exception ex)
                {
                }

                if (world.getWeatherDuration() > 0)
                {
                    world.setThundering(false);
                    world.setWeatherDuration(0);
                }
                else if (world.getThunderDuration() > 0)
                {
                    world.setStorm(false);
                    world.setThunderDuration(0);
                }
            }
        }
    }
}
