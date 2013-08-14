package me.StevenLawson.TotalFreedomMod;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TFM_WorldEditBridge
{
    private WorldEditPlugin worldEditPlugin = null;

    private TFM_WorldEditBridge()
    {
    }

    public WorldEditPlugin getWorldEditPlugin()
    {
        if (this.worldEditPlugin == null)
        {
            try
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
            catch (Exception e)
            {
                TFM_Log.severe(e);
            }
        }
        return this.worldEditPlugin;
    }

    public BukkitPlayer getBukkitPlayer(Player player)
    {
        try
        {
            WorldEditPlugin wep = this.getWorldEditPlugin();
            if (wep != null)
            {
                return wep.wrapPlayer(player);
            }
        }
        catch (Exception e)
        {
            TFM_Log.severe(e);
        }
        return null;
    }

    public LocalSession getPlayerSession(Player player)
    {
        try
        {
            WorldEditPlugin wep = this.getWorldEditPlugin();
            if (wep != null)
            {
                return wep.getSession(player);
            }
        }
        catch (Exception e)
        {
            TFM_Log.severe(e);
        }
        return null;
    }

    public void undo(Player player, int count)
    {
        try
        {
            LocalSession session = getPlayerSession(player);
            if (session != null)
            {
                BukkitPlayer bukkitPlayer = this.getBukkitPlayer(player);
                if (bukkitPlayer != null)
                {
                    for (int i = 0; i < count; i++)
                    {
                        session.undo(session.getBlockBag(bukkitPlayer), bukkitPlayer);
                    }
                }
            }
        }
        catch (Exception e)
        {
            TFM_Log.severe(e);
        }
    }

    public void setLimit(Player player, int limit)
    {
        try
        {
            LocalSession session = getPlayerSession(player);
            if (session != null)
            {
                session.setBlockChangeLimit(limit);
            }
        }
        catch (Exception e)
        {
            TFM_Log.severe(e);
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
