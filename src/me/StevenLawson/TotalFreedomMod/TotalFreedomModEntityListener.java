package me.StevenLawson.TotalFreedomMod;

import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class TotalFreedomModEntityListener extends EntityListener
{
    public static TotalFreedomMod plugin;
    
    TotalFreedomModEntityListener(TotalFreedomMod instance)
    {
        plugin = instance;
    }

    @Override
    public void onEntityExplode(EntityExplodeEvent event)
    {
        event.setCancelled(!plugin.allowExplosions);
    }
}
