package me.totalfreedom.totalfreedommod.bridge;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class WorldGuardBridge extends FreedomService
{

    private WorldGuardPlugin worldGuardPlugin;

    public WorldGuardBridge(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    public WorldGuardPlugin getWorldGuardPlugin()
    {
        if (worldGuardPlugin == null)
        {
            try
            {
                final Plugin worldGuard = server.getPluginManager().getPlugin("WorldGuard");
                if (worldGuard != null && worldGuard instanceof WorldGuardPlugin)
                {
                    worldGuardPlugin = (WorldGuardPlugin)worldGuard;
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }

        return worldGuardPlugin;
    }

    public int wipeRegions(World world)
    {
        int count = 0;
        RegionManager regionManager = getWorldGuardPlugin().getRegionManager(world);
        if (regionManager != null)
        {
            Map<String, ProtectedRegion> regions = regionManager.getRegions();
            for (ProtectedRegion region : regions.values())
            {
                regionManager.removeRegion(region.getId());
                count++;
            }
        }
        return count;
    }

    public boolean isEnabled()
    {
        final WorldGuardPlugin wg = getWorldGuardPlugin();

        return wg != null && wg.isEnabled();
    }
}