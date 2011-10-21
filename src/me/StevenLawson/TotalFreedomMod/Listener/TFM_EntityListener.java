package me.StevenLawson.TotalFreedomMod.Listener;

import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class TFM_EntityListener extends EntityListener
{
    private TotalFreedomMod plugin;

    public TFM_EntityListener(TotalFreedomMod instance)
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
        
        event.setYield(0.0f);
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
                TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p, plugin);
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
        
        if (event.getCause() == DamageCause.LAVA && !plugin.allowLavaDamage)
        {
            event.setCancelled(true);
            return;
        }
    }
}
