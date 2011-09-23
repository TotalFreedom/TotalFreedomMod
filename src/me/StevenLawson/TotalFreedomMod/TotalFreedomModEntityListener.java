package me.StevenLawson.TotalFreedomMod;

import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
        if (!plugin.allowExplosions)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onEntityCombust(EntityCombustEvent event)
    {
        if (!plugin.allowFireDamage)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.getCause() == DamageCause.LAVA && !plugin.allowLavaDamage)
        {
            event.setCancelled(true);
            return;
        }
    }
}
