package me.totalfreedom.totalfreedommod.world;

import lombok.Getter;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.pravian.aero.component.PluginComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class TFM_CustomWorld extends PluginComponent<TotalFreedomMod>
{
    @Getter
    private final String name;
    //
    private World world;

    public TFM_CustomWorld(String name)
    {
        this.name = name;
    }

    public final World getWorld()
    {
        if (world == null || !Bukkit.getWorlds().contains(world))
        {
            world = generateWorld();
        }

        if (world == null)
        {
            FLog.warning("Could not load world: " + name);
        }

        return world;
    }

    public void sendToWorld(Player player)
    {
        try
        {
            player.teleport(getWorld().getSpawnLocation());
        }
        catch (Exception ex)
        {
            player.sendMessage(ex.getMessage());
        }
    }

    protected abstract World generateWorld();
}
