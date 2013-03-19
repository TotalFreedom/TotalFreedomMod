package me.StevenLawson.TotalFreedomMod;

public class TFM_TickMeter
{
    int ticks;
    int taskId;
    final TotalFreedomMod plugin;

    public TFM_TickMeter(TotalFreedomMod plugin)
    {
        this.plugin = plugin;
    }

    public int startTicking()
    {
        int tId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                ticks += 1;
            }
        }, 1L, 1L); // ticks (20 in 1 second)

        taskId = tId;
        return tId;
    }

    public void stopTicking()
    {
        plugin.getServer().getScheduler().cancelTask(taskId);
    }

    public int getTicks()
    {
        return ticks;
    }
}
