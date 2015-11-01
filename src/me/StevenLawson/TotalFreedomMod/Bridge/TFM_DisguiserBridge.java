package me.StevenLawson.TotalFreedomMod.Bridge;

import java.util.Collection;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

public class TFM_DisguiserBridge
{
    private TFM_DisguiserBridge()
    {
    }

    public static boolean undisguisePlayer(Player player)
    {
        if (!disguiseCraftEnabled())
        {
            return false;
        }

        try
        {
            DisguiseCraftAPI api = DisguiseCraft.getAPI();
            if (api != null)
            {
                return api.undisguisePlayer(player);
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }

        return false;
    }

    public static void undisguiseAllPlayers()
    {
        if (!disguiseCraftEnabled())
        {
            return;
        }

        try
        {
            DisguiseCraftAPI api = DisguiseCraft.getAPI();
            if (api != null)
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    api.undisguisePlayer(player);
                }
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static boolean disguiseCraftEnabled()
    {
        boolean pluginEnabled = false;
        try
        {
            pluginEnabled = Bukkit.getPluginManager().isPluginEnabled("DisguiseCraft");
        }
        catch (Exception ex)
        {
        }
        return pluginEnabled;
    }
}