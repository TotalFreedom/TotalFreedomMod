package me.totalfreedom.totalfreedommod;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MovementValidator extends FreedomService
{

    public static final int MAX_XYZ_COORD = 29999998;

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
        if (Math.abs(event.getTo().getX()) >= MAX_XYZ_COORD || Math.abs(event.getTo().getZ()) >= MAX_XYZ_COORD || Math.abs(event.getTo().getY()) >= MAX_XYZ_COORD)
        {
            event.setCancelled(true); // illegal position, cancel it
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        final Player player = event.getPlayer();

        // Check absolute value to account for negatives
        if (Math.abs(event.getTo().getX()) >= MAX_XYZ_COORD || Math.abs(event.getTo().getZ()) >= MAX_XYZ_COORD || Math.abs(event.getTo().getY()) >= MAX_XYZ_COORD)
        {
            event.setCancelled(true);
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final Player player = event.getPlayer();

        // Validate position
        if (Math.abs(player.getLocation().getX()) >= MAX_XYZ_COORD || Math.abs(player.getLocation().getZ()) >= MAX_XYZ_COORD || Math.abs(player.getLocation().getY()) >= MAX_XYZ_COORD)
        {
            player.teleport(player.getWorld().getSpawnLocation()); // Illegal position, teleport to spawn
        }
    }

}
