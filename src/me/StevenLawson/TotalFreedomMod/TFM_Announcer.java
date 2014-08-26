package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_Announcer
{
    private static final List<String> ANNOUNCEMENTS = new ArrayList<String>();
    private static boolean enabled;
    private static long interval;
    private static String prefix;
    private static BukkitRunnable announcer;

    private TFM_Announcer()
    {
        throw new AssertionError();
    }

    public static boolean isEnabled()
    {
        return enabled;
    }

    public static List<String> getAnnouncements()
    {
        return Collections.unmodifiableList(ANNOUNCEMENTS);
    }

    public static long getTickInterval()
    {
        return interval;
    }

    public static String getPrefix()
    {
        return prefix;
    }

    public static void load()
    {
        stop();

        ANNOUNCEMENTS.clear();

        for (Object announcement : TFM_ConfigEntry.ANNOUNCER_ANNOUNCEMENTS.getList())
        {
            ANNOUNCEMENTS.add(TFM_Util.colorize((String) announcement));
        }

        enabled = TFM_ConfigEntry.ANNOUNCER_ENABLED.getBoolean();
        interval = TFM_ConfigEntry.ANNOUNCER_INTERVAL.getInteger() * 20L;
        prefix = TFM_Util.colorize(TFM_ConfigEntry.ANNOUNCER_PREFIX.getString());

        if (enabled)
        {
            start();
        }
    }

    public static boolean isStarted()
    {
        return announcer != null;
    }

    public static void start()
    {
        if (isStarted())
        {
            return;
        }

        announcer = new BukkitRunnable()
        {
            private int current = 0;

            @Override
            public void run()
            {
                current++;

                if (current >= ANNOUNCEMENTS.size())
                {
                    current = 0;
                }

                TFM_Util.bcastMsg(prefix + ANNOUNCEMENTS.get(current));
            }
        };

        announcer.runTaskTimer(TotalFreedomMod.plugin, interval, interval);
    }

    public static void stop()
    {
        if (announcer == null)
        {
            return;
        }

        try
        {
            announcer.cancel();
        }
        finally
        {
            announcer = null;
        }
    }
}
