package me.StevenLawson.TotalFreedomMod;

import org.bukkit.entity.Player;

public class TFM_WorldEditBridge
{
    private TFM_WorldEditBridge()
    {
    }

    public void undo(Player player, int count)
    {
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
