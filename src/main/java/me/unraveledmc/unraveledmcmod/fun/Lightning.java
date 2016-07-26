package me.unraveledmc.unraveledmcmod.fun;

import me.unraveledmc.unraveledmcmod.FreedomService;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

public class Lightning extends FreedomService
{
     public static List<Player> lpl = new ArrayList();
     public static int amount = 1;

    public Lightning(UnraveledMCMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        if (lpl.contains(p))
        {
            Location l = p.getTargetBlock((Set<Material>)null, 600).getLocation();
            for (int i = 0; i < amount; i++)
            {
                p.getWorld().strikeLightning(l);
            }
        }
    }
}
