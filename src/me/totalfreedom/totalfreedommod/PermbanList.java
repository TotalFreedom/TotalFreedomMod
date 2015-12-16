package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FLog;
import com.google.common.collect.Sets;
import java.util.Set;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.config.FConfig;
import net.pravian.aero.component.service.AbstractService;

public class PermbanList extends AbstractService<TotalFreedomMod>
{
    @Getter
    private final Set<String> permbannedNames = Sets.newHashSet();
    @Getter
    private final Set<String> permbannedIps = Sets.newHashSet();

    public PermbanList(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        permbannedNames.clear();
        permbannedIps.clear();

        final FConfig config = new FConfig(TotalFreedomMod.plugin, TotalFreedomMod.PERMBAN_FILENAME, true);
        config.load();

        for (String name : config.getKeys(false))
        {
            permbannedNames.add(name.toLowerCase().trim());
            permbannedIps.addAll(config.getStringList(name));
        }

        FLog.info("Loaded " + permbannedNames.size() + " permanently banned usernames and " + permbannedIps.size() + " permanently banned IPs.");
    }

    @Override
    protected void onStop()
    {
    }

}
