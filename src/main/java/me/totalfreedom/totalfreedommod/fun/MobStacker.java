package me.totalfreedom.totalfreedommod.fun;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MobStacker extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player || !(event.getDamager() instanceof Player))
        {
            return;
        }

        Player attacker = (Player)event.getDamager();

        if (!plugin.al.isAdmin(attacker))
        {
            return;
        }

        ItemStack item = attacker.getInventory().getItemInMainHand();

        if (item != null && item.getType().equals(Material.POTATO))
        {
            event.setCancelled(true);
            attacker.addPassenger(event.getEntity());
        }
    }
}
