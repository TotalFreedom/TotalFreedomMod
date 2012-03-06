package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.*;

public class TFM_WeatherListener implements Listener
{
    private final TotalFreedomMod plugin;
    private final Server server;

    public TFM_WeatherListener(TotalFreedomMod instance)
    {
        this.plugin = instance;
        this.server = plugin.getServer();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onThunderChange(ThunderChangeEvent event)
    {
        if (event.toThunderState() && TotalFreedomMod.disableWeather)
        {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event)
    {
        if (event.toWeatherState() && TotalFreedomMod.disableWeather)
        {
            event.setCancelled(true);
            return;
        }
    }
}
