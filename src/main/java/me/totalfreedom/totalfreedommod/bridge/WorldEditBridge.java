package me.totalfreedom.totalfreedommod.bridge;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldEditBridge extends FreedomService
{

    private final WorldEditListener listener;
    //
    private WorldEditPlugin worldeditPlugin = null;

    public WorldEditBridge(TotalFreedomMod plugin)
    {
        super(plugin);
        listener = new WorldEditListener(plugin);
    }

    @Override
    protected void onStart()
    {
        listener.register();
    }

    @Override
    protected void onStop()
    {
        listener.unregister();
    }

    public WorldEditPlugin getWorldEditPlugin()
    {
        if (worldeditPlugin == null)
        {
            try
            {
                Plugin we = server.getPluginManager().getPlugin("WorldEdit");
                if (we != null)
                {
                    if (we instanceof WorldEditPlugin)
                    {
                        worldeditPlugin = (WorldEditPlugin)we;
                    }
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }

        return worldeditPlugin;
    }

    public void undo(Player player, int count)
    {
        try
        {
            LocalSession session = getPlayerSession(player);
            if (session != null)
            {
                final BukkitPlayer bukkitPlayer = getBukkitPlayer(player);
                if (bukkitPlayer != null)
                {
                    for (int i = 0; i < count; i++)
                    {
                        com.sk89q.worldedit.entity.Player fuckyou = (com.sk89q.worldedit.entity.Player)bukkitPlayer;
                        session.undo(session.getBlockBag(fuckyou), fuckyou);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    public void redo(Player player, int count)
    {
        try
        {
            LocalSession session = getPlayerSession(player);
            if (session != null)
            {
                final BukkitPlayer bukkitPlayer = getBukkitPlayer(player);
                if (bukkitPlayer != null)
                {
                    for (int i = 0; i < count; i++)
                    {
                        com.sk89q.worldedit.entity.Player fuckyou = (com.sk89q.worldedit.entity.Player)bukkitPlayer;
                        session.redo(session.getBlockBag(fuckyou), fuckyou);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    public void setLimit(Player player, int limit)
    {
        try
        {
            final LocalSession session = getPlayerSession(player);
            if (session != null)
            {
                session.setBlockChangeLimit(limit);
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }

    }

    public int getDefaultLimit()
    {
        final WorldEditPlugin wep = getWorldEditPlugin();
        if (wep == null)
        {
            return 0;
        }

        return wep.getLocalConfiguration().defaultChangeLimit;

    }

    public int getMaxLimit()
    {
        final WorldEditPlugin wep = getWorldEditPlugin();
        if (wep == null)
        {
            return 0;
        }

        return wep.getLocalConfiguration().maxChangeLimit;

    }

    private LocalSession getPlayerSession(Player player)
    {
        final WorldEditPlugin wep = getWorldEditPlugin();
        if (wep == null)
        {
            return null;
        }

        try
        {
            return wep.getSession(player);
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
            return null;
        }
    }

    private BukkitPlayer getBukkitPlayer(Player player)
    {
        final WorldEditPlugin wep = getWorldEditPlugin();
        if (wep == null)
        {
            return null;
        }

        try
        {
            return wep.wrapPlayer(player);
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
            return null;
        }
    }
}
