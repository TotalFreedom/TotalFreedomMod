package me.StevenLawson.TotalFreedomMod.Bridge;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TFM_WorldEditBridge
{
    private static WorldEditPlugin worldEditPlugin = null;

    private TFM_WorldEditBridge()
    {
        throw new AssertionError();
    }

    private static WorldEditPlugin getWorldEditPlugin()
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

    private static LocalSession getPlayerSession(Player player)
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

    private static BukkitPlayer getBukkitPlayer(Player player)
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
}
