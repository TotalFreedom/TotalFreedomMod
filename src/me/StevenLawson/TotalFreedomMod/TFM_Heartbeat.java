package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_Heartbeat extends BukkitRunnable
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
