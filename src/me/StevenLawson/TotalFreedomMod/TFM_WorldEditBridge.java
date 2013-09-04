package me.StevenLawson.TotalFreedomMod;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
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
            catch (Exception ex)
            {
                TFM_Log.severe(ex);
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
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
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
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
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
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
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
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public void validateSelection(Player player)
    {
        try
        {
            LocalSession session = getPlayerSession(player);
            if (session != null)
            {
                LocalWorld selectionWorld = session.getSelectionWorld();
                Region selection = session.getSelection(selectionWorld);
                if (TFM_ProtectedArea.isInProtectedArea(
                        getBukkitVector(selection.getMinimumPoint()),
                        getBukkitVector(selection.getMaximumPoint()),
                        selectionWorld.getName()))
                {
                    TFM_Util.bcastMsg("(DEBUG MSG: " + player.getName() + " has selected part of a protected area.");
                }
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    private static org.bukkit.util.Vector getBukkitVector(com.sk89q.worldedit.Vector worldEditVector)
    {
        return new org.bukkit.util.Vector(worldEditVector.getX(), worldEditVector.getY(), worldEditVector.getZ());
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
