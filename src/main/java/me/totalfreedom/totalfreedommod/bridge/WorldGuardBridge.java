package me.totalfreedom.totalfreedommod.bridge;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.Map;

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
                if (worldGuard != null)
                {
                    if (worldGuard instanceof WorldGuardPlugin)
                    {
                        worldGuardPlugin = (WorldGuardPlugin) worldGuard;
                    }
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }

        return worldGuardPlugin;
    }

    public Boolean wipeRegions(World world)
    {
        RegionContainer container = getWorldGuardPlugin().getRegionContainer();
        RegionManager rm = container.get(world);
        if (rm != null)
        {
            Map<String, ProtectedRegion> regions = rm.getRegions();
            for (ProtectedRegion region : regions.values())
            {
                rm.removeRegion(region.getId());
            }
            return true;
        }
        return false;
    }

    public boolean isPluginEnabled() {
        Plugin wr = getWorldGuardPlugin();

        return wr != null && wr.isEnabled();
    }
}