package me.StevenLawson.TotalFreedomMod;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitServerInterface;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TFM_WorldEditBridge
{
    private WorldEditPlugin worldEditPlugin = null;
    private WorldEditAPI worldEditAPI = null;
    private BukkitServerInterface bukkitServerInterface = null;

    private TFM_WorldEditBridge()
    {
    }

    public WorldEditPlugin getWorldEditPlugin()
    {
        if (this.worldEditPlugin == null)
        {
            Plugin we = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            if (we != null)
            {
                if (we instanceof WorldEditPlugin)
                {
                    this.worldEditPlugin = (WorldEditPlugin) we;
                }
            }
        }

        return this.worldEditPlugin;
    }

    public WorldEditAPI getWorldEditAPI()
    {
        if (this.worldEditAPI == null)
        {
            WorldEditPlugin wep = getWorldEditPlugin();
            if (wep != null)
            {
                this.worldEditAPI = new WorldEditAPI(wep);
            }
        }

        return this.worldEditAPI;
    }

    public BukkitServerInterface getBukkitServerInterface()
    {
        if (this.bukkitServerInterface == null)
        {
            WorldEditPlugin wep = this.getWorldEditPlugin();
            if (wep != null)
            {
                this.bukkitServerInterface = new BukkitServerInterface(wep, Bukkit.getServer());
            }
        }

        return this.bukkitServerInterface;
    }

    public BukkitPlayer getBukkitPlayer(Player p)
    {
        WorldEditPlugin wep = this.getWorldEditPlugin();
        BukkitServerInterface bsi = this.getBukkitServerInterface();

        if (wep != null && bsi != null)
        {
            return new BukkitPlayer(wep, bsi, p);
        }

        return null;
    }

    public void undo(Player p, int count)
    {
        try
        {
            WorldEditAPI api = this.getWorldEditAPI();
            if (api != null)
            {
                LocalSession session = api.getSession(p);
                if (session != null)
                {
                    BukkitPlayer bukkitPlayer = this.getBukkitPlayer(p);
                    for (int i = 0; i < count; i++)
                    {
                        session.undo(session.getBlockBag(bukkitPlayer), bukkitPlayer);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public void setLimit(Player p, int limit)
    {
        try
        {
            WorldEditAPI api = this.getWorldEditAPI();
            if (api != null)
            {
                LocalSession session = api.getSession(p);
                if (session != null)
                {
                    session.setBlockChangeLimit(limit);
                }
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static TFM_WorldEditBridge getInstance()
    {
        return TFM_WorldEditBridgeHolder.INSTANCE;
    }

    private static class TFM_WorldEditBridgeHolder
    {
        private static final TFM_WorldEditBridge INSTANCE = new TFM_WorldEditBridge();
    }
}
