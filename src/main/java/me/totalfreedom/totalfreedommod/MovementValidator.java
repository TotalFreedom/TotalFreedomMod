package me.totalfreedom.totalfreedommod;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MovementValidator extends FreedomService
{

    public static final int MAX_XZ_COORD = 30000000;
    public static final int MAX_Y_COORD = 29999000;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        // Check absolute value to account for negatives
        if (Math.abs(event.getTo().getX()) >= MAX_XZ_COORD || Math.abs(event.getTo().getZ()) >= MAX_XZ_COORD || Math.abs(event.getTo().getY()) >= MAX_Y_COORD)
        {
            event.setCancelled(true); // illegal position, cancel it
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();
        // Validate position
        if (Math.abs(player.getLocation().getX()) >= MAX_XZ_COORD || Math.abs(player.getLocation().getZ()) >= MAX_XZ_COORD || Math.abs(player.getLocation().getY()) >= MAX_Y_COORD)
        {
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        // Validate position
        if (Math.abs(player.getLocation().getX()) >= MAX_XZ_COORD || Math.abs(player.getLocation().getZ()) >= MAX_XZ_COORD || Math.abs(player.getLocation().getY()) >= MAX_Y_COORD)
        {
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }
}
