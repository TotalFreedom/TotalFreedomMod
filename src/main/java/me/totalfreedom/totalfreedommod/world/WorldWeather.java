package me.totalfreedom.totalfreedommod.world;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.World;

public enum WorldWeather
{

    OFF("off"),
    RAIN("rain"),
    STORM("storm,thunderstorm");
    //
    private final List<String> aliases;

    private WorldWeather(String aliases)
    {
        this.aliases = Arrays.asList(StringUtils.split(aliases, ","));
    }

    public void setWorldToWeather(World world)
    {
        world.setStorm(this == RAIN || this == STORM);
        world.setWeatherDuration(this == RAIN || this == STORM ? 20 * 60 * 5 : 0);

        world.setThundering(this == STORM);
        world.setThunderDuration(this == STORM ? 20 * 60 * 5 : 0);
    }

    public static WorldWeather getByAlias(String needle)
    {
        needle = needle.toLowerCase();
        for (WorldWeather mode : values())
        {
            if (mode.aliases.contains(needle))
            {
                return mode;
            }
        }
        return null;
    }
}
