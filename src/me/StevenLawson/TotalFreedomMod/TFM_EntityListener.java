package me.StevenLawson.TotalFreedomMod;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class TFM_EntityListener extends EntityListener
{
    private TotalFreedomMod plugin;

    TFM_EntityListener(TotalFreedomMod instance)
    {
        this.plugin = instance;
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
    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        if (!plugin.allowExplosions)
        {
            event.setCancelled(true);
            return;
        }

        event.setRadius((float) plugin.explosiveRadius);
    }

    @Override
    public void onEntityCombust(EntityCombustEvent event)
    {
        if (!plugin.allowFireSpread)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player p = (Player) event.getEntity();
            if (p != null)
            {
                TFM_UserInfo playerdata = plugin.userinfo.get(p);
                if (playerdata != null)
                {
                    if (playerdata.getForcedDeath())
                    {
                        event.setCancelled(false);
                        p.setFoodLevel(0);
                        p.setHealth(0);
                        event.setDamage(100);
                        playerdata.setForcedDeath(false);
                        return;
                    }
                }
            }
        }
        
        if (event.getCause() == DamageCause.LAVA && !plugin.allowLavaDamage)
        {
            event.setCancelled(true);
            return;
        }
    }
}
