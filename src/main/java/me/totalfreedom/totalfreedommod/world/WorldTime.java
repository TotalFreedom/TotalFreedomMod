package me.totalfreedom.totalfreedommod.world;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.World;

public enum WorldTime
{

    INHERIT(),
    SUNRISE("sunrise,morning", 0),
    NOON("noon,midday,day", 6000),
    SUNSET("sunset,evening", 12000),
    MIDNIGHT("midnight,night", 18000);
    //
    private final int timeTicks;
    private final List<String> aliases;

    private WorldTime()
    {
        this.timeTicks = 0;
        this.aliases = null;
    }

    private WorldTime(String aliases, int timeTicks)
    {
        this.timeTicks = timeTicks;
        this.aliases = Arrays.asList(StringUtils.split(aliases, ","));
    }

    public int getTimeTicks()
    {
        return timeTicks;
    }

    public void setWorldToTime(World world)
    {
        long time = world.getTime();
        time -= time % 24000;
        world.setTime(time + 24000 + getTimeTicks());
    }

    public static WorldTime getByAlias(String needle)
    {
        needle = needle.toLowerCase();
        for (WorldTime time : values())
        {
            if (time.aliases != null && time.aliases.contains(needle))
            {
                return time;
            }
        }
        return null;
    }
}
