package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class TFM_WeatherListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH)
    public void onThunderChange(ThunderChangeEvent event)
    {
        if (event.toThunderState() && TFM_ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event)
    {
        if (event.toWeatherState() && TFM_ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            event.setCancelled(true);
            return;
        }
    }
}
