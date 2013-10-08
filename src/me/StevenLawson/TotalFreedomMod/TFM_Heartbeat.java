package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_Heartbeat extends BukkitRunnable
{
    private static final long AUTO_KICK_TIME = (long) TFM_ConfigEntry.AUTOKICK_TIME.getInteger() * 1000L;
    private final TotalFreedomMod plugin;
    private final Server server;
    private static Long lastRan = null;

    public TFM_Heartbeat(TotalFreedomMod instance)
    {
        this.plugin = instance;
        this.server = plugin.getServer();
    }

    public static Long getLastRan()
    {
        return lastRan;
    }

    @Override
    public void run()
    {
        lastRan = System.currentTimeMillis();

        final TFM_EssentialsBridge essentialsBridge = TFM_EssentialsBridge.getInstance();
        final boolean doAwayKickCheck =
                TFM_ConfigEntry.AUTOKICK_ENABLED.getBoolean()
                && essentialsBridge.isEssentialsEnabled()
                && ((server.getOnlinePlayers().length / server.getMaxPlayers()) > TFM_ConfigEntry.AUTOKICK_THRESHOLD.getDouble());

        for (Player player : server.getOnlinePlayers())
        {
            final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
            playerdata.resetMsgCount();
            playerdata.resetBlockDestroyCount();
            playerdata.resetBlockPlaceCount();

            if (doAwayKickCheck)
            {
                final long lastActivity = essentialsBridge.getLastActivity(player.getName());
                if (lastActivity > 0 && lastActivity + AUTO_KICK_TIME < System.currentTimeMillis())
                {
                    player.kickPlayer("Automatically kicked by server for inactivity.");
                }
            }
        }

        if (TFM_ConfigEntry.AUTO_ENTITY_WIPE.getBoolean())
        {
            TFM_Util.TFM_EntityWiper.wipeEntities(!TFM_ConfigEntry.ALLOW_EXPLOSIONS.getBoolean(), false);
        }

        if (TFM_ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            for (World world : server.getWorlds())
            {
                try
                {
                    if (world == TFM_AdminWorld.getInstance().getWorld() && TFM_AdminWorld.getInstance().getWeatherMode() != TFM_AdminWorld.WeatherMode.OFF)
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
