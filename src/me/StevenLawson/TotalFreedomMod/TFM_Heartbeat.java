package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TFM_Heartbeat implements Runnable
{
    private final TotalFreedomMod plugin;
    private final Server server;

    public TFM_Heartbeat(TotalFreedomMod instance)
    {
        this.plugin = instance;
        this.server = plugin.getServer();
    }

    @Override
    public void run()
    {
        for (Player p : server.getOnlinePlayers())
        {
            TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);
            playerdata.resetMsgCount();
            playerdata.resetBlockDestroyCount();
            playerdata.resetBlockPlaceCount();
        }

        if (TotalFreedomMod.autoEntityWipe)
        {
            TFM_Util.wipeEntities(!TotalFreedomMod.allowExplosions, false);
        }

        if (TotalFreedomMod.disableNight)
        {
            try
            {
                for (World world : server.getWorlds())
                {
                    if (world.getTime() > 12000L)
                    {
                        TFM_Util.setWorldTime(world, 1000L);
                    }
                }
            }
            catch (NullPointerException ex)
            {
            }
        }

        if (TotalFreedomMod.disableWeather)
        {
            for (World world : server.getWorlds())
            {
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
