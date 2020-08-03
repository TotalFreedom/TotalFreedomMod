package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.command.Command_sit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityDismountEvent;

public class Sitter extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e)
    {
        Entity dm = e.getDismounted();
        if (dm instanceof ArmorStand)
        {
            if (Command_sit.STANDS.contains(dm))
            {
                Command_sit.STANDS.remove(dm);
                dm.remove();
            }
        }
    }
}
