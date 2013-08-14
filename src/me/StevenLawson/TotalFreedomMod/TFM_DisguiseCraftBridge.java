package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

public class TFM_DisguiseCraftBridge
{
    private TFM_DisguiseCraftBridge()
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
                Player[] players = Bukkit.getOnlinePlayers();
                for (Player player : players)
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
