package me.StevenLawson.TotalFreedomMod;

import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;

public class TotalFreedomModBlockListener extends BlockListener
{
    public static TotalFreedomMod plugin;
    
    TotalFreedomModBlockListener(TotalFreedomMod instance)
    {
        plugin = instance;
    }

    @Override
    public void onBlockBurn(BlockBurnEvent event)
    {
        if (!plugin.allowFireDamage)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        if (!plugin.allowFireDamage)
        {
            event.setCancelled(true);
            return;
        }
    }
}
