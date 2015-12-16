package me.totalfreedom.totalfreedommod.listener;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.world.WorldWeather;
import net.pravian.aero.component.PluginListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherListener extends PluginListener<TotalFreedomMod>
{
    @EventHandler(priority = EventPriority.HIGH)
    public void onThunderChange(ThunderChangeEvent event)
    {
        try
        {
            if (event.getWorld() == plugin.wm.adminworld.getWorld()
                    && plugin.wm.adminworld.getWeatherMode() != WorldWeather.OFF)
            {
                return;
            }
        }
        catch (Exception ex)
        {
        }

        if (event.toThunderState() && ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event)
    {
        try
        {
            if (event.getWorld() == plugin.wm.adminworld.getWorld() && plugin.wm.adminworld.getWeatherMode() != WorldWeather.OFF)
            {
                return;
            }
        }
        catch (Exception ex)
        {
        }

        if (event.toWeatherState() && ConfigEntry.DISABLE_WEATHER.getBoolean())
        {
            event.setCancelled(true);
            return;
        }
    }
}
