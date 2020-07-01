package me.totalfreedom.totalfreedommod.bridge;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class WorldGuardBridge extends FreedomService
{
    @Override
    public void onStart()
    {
        plugin.wr.protectWorld(plugin.wm.hubworld.getWorld());
        plugin.wr.protectWorld(plugin.wm.masterBuilderWorld.getWorld());
    }

    @Override
    public void onStop()
    {
    }

    public RegionManager getRegionManager(World world)
    {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(world));
    }

    public int wipeRegions(World world)
    {
        int count = 0;
        RegionManager regionManager = getRegionManager(world);
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
        Plugin plugin = server.getPluginManager().getPlugin("WorldGuard");

        return plugin != null && plugin.isEnabled();
    }
}