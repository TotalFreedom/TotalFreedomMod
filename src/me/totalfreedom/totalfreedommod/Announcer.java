package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FUtil;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import net.pravian.aero.component.service.AbstractService;
import org.bukkit.scheduler.BukkitRunnable;

public class Announcer extends AbstractService<TotalFreedomMod>
{
    private final List<String> announcements = Lists.newArrayList();
    @Getter
    private boolean enabled;
    @Getter
    private long interval;
    @Getter
    private String prefix;
    private BukkitRunnable announcer;

    public Announcer(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        enabled = ConfigEntry.ANNOUNCER_ENABLED.getBoolean();
        interval = ConfigEntry.ANNOUNCER_INTERVAL.getInteger() * 20L;
        prefix = FUtil.colorize(ConfigEntry.ANNOUNCER_PREFIX.getString());

        announcements.clear();
        for (Object announcement : ConfigEntry.ANNOUNCER_ANNOUNCEMENTS.getList())
        {
            announcements.add(FUtil.colorize((String) announcement));
        }

        if (!enabled)
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

                if (current >= announcements.size())
                {
                    current = 0;
                }

                FUtil.bcastMsg(prefix + announcements.get(current));
            }
        };

        announcer.runTaskTimer(TotalFreedomMod.plugin, interval, interval);
    }

    @Override
    protected void onStop()
    {
        if (announcer == null)
        {
            return;
        }

        try
        {
            announcer.cancel();
        }
        catch (Exception ignored)
        {
        }
        finally
        {
            announcer = null;
        }
    }

    public List<String> getAnnouncements()
    {
        return Collections.unmodifiableList(announcements);
    }

}
