package me.StevenLawson.TotalFreedomMod;

import org.bukkit.event.weather.*;

class TFM_WeatherListener extends WeatherListener
{
    private TotalFreedomMod plugin;

    public TFM_WeatherListener(TotalFreedomMod instance)
    {
        this.plugin = instance;
    }
    
    @Override
    public void onThunderChange(ThunderChangeEvent event)
    {
        if (event.toThunderState() && plugin.disableWeather)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onWeatherChange(WeatherChangeEvent event)
    {
        if (event.toWeatherState() && plugin.disableWeather)
        {
            event.setCancelled(true);
            return;
        }
    }
}
