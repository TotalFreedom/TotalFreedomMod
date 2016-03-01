package me.totalfreedom.totalfreedommod;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MovementValidator extends FreedomService
{

    public static final int MAX_XZ_COORD = 30000000;

    public MovementValidator(TotalFreedomMod plugin)
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
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        // Check absolute value to account for negatives
        if (Math.abs(event.getTo().getX()) >= MAX_XZ_COORD || Math.abs(event.getTo().getZ()) >= MAX_XZ_COORD)
        {
            event.setCancelled(true); // illegal position, cancel it
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final Player player = event.getPlayer();

        // Validate position
        if (Math.abs(player.getLocation().getX()) >= MAX_XZ_COORD || Math.abs(player.getLocation().getZ()) >= MAX_XZ_COORD)
        {
            player.teleport(player.getWorld().getSpawnLocation()); // Illegal position, teleport to spawn
        }
    }

}
