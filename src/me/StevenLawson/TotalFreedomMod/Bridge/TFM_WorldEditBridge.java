package me.StevenLawson.TotalFreedomMod.Bridge;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_ProtectedArea;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_WorldEditBridge
{
    private static WorldEditPlugin worldEditPlugin = null;

    private TFM_WorldEditBridge()
    {
        throw new AssertionError();
    }

    public static WorldEditPlugin getWorldEditPlugin()
    {
        if (worldEditPlugin == null)
        {
            try
            {
                Plugin we = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
                if (we != null)
                {
                    if (we instanceof WorldEditPlugin)
                    {
                        worldEditPlugin = (WorldEditPlugin) we;
                    }
                }
            }
            catch (Exception ex)
            {
                TFM_Log.severe(ex);
            }
        }
        return worldEditPlugin;
    }

    public static BukkitPlayer getBukkitPlayer(Player player)
    {
        try
        {
            final WorldEditPlugin wep = getWorldEditPlugin();
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

    public static LocalSession getPlayerSession(Player player)
    {
        try
        {
            final WorldEditPlugin wep = getWorldEditPlugin();
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

    public static void undo(Player player, int count)
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

    public static void setLimit(Player player, int limit)
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
            TFM_Log.severe(ex);
        }
    }

    public static void validateSelection(final Player player)
    {
        if (TFM_AdminList.isSuperAdmin(player))
        {
            return;
        }

        try
        {
            final LocalSession session = getPlayerSession(player);

            if (session == null)
            {
                return;
            }

            final World selectionWorld = session.getSelectionWorld();
            final Region selection = session.getSelection(selectionWorld);

            if (TFM_ProtectedArea.isInProtectedArea(
                    getBukkitVector(selection.getMinimumPoint()),
                    getBukkitVector(selection.getMaximumPoint()),
                    selectionWorld.getName()))
            {
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        player.sendMessage(ChatColor.RED + "The region that you selected contained a protected area. Selection cleared.");
                        session.getRegionSelector(selectionWorld).clear();
                    }
                }.runTask(TotalFreedomMod.plugin);
            }

        }
        catch (IncompleteRegionException ex)
        {
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
}
