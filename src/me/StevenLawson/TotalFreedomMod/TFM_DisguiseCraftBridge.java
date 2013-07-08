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

    public boolean undisguisePlayer(Player player)
    {
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

    public void undisguiseAllPlayers()
    {
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

    public static TFM_DisguiseCraftBridge getInstance()
    {
        return TFM_DisguiseCraftBridgeHolder.INSTANCE;
    }

    private static class TFM_DisguiseCraftBridgeHolder
    {
        private static final TFM_DisguiseCraftBridge INSTANCE = new TFM_DisguiseCraftBridge();
    }
}
