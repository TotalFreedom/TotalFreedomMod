package me.unraveledmc.unraveledmcmod.blocking;

import me.unraveledmc.unraveledmcmod.FreedomService;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.player.FPlayer;
import me.unraveledmc.unraveledmcmod.util.FLog;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.Action;

public class InteractBlocker extends FreedomService
{

    public InteractBlocker(UnraveledMCMod plugin)
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
        final Player player = event.getPlayer();
        final FPlayer playerdata = plugin.pl.getPlayer(player);

        switch (event.getAction())
        {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
            {
                handleRightClick(event);
                break;
            }

            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
            {
                //
                break;
            }
        }
    }

    private void handleRightClick(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();

        switch (event.getMaterial())
        {
            case WATER_BUCKET:
            {
                if (plugin.al.isAdmin(player) || ConfigEntry.ALLOW_WATER_PLACE.getBoolean())
                {
                    break;
                }

                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "Water buckets are currently disabled.");
                event.setCancelled(true);
                break;
            }

            case LAVA_BUCKET:
            {
                if (plugin.al.isAdmin(player) || ConfigEntry.ALLOW_LAVA_PLACE.getBoolean())
                {
                    break;
                }

                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "Lava buckets are currently disabled.");
                event.setCancelled(true);
                break;
            }

            case EXPLOSIVE_MINECART:
            {
                if (ConfigEntry.ALLOW_TNT_MINECARTS.getBoolean())
                {
                    break;
                }

                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                player.sendMessage(ChatColor.GRAY + "TNT minecarts are currently disabled.");
                event.setCancelled(true);
                break;
            }
        }
    }
    
     @EventHandler(priority = EventPriority.HIGH)
    public void onSignRightClick(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            switch(event.getClickedBlock().getType())
            {
                case SIGN:
                case SIGN_POST:
                case WALL_SIGN:
                {
                    FLog.warning(event.getPlayer().getName() + " just right clicked a possible command sign at " + FUtil.formatLocation(event.getClickedBlock().getLocation()));
                    for (Player player : server.getOnlinePlayers())
                    {
                        if (plugin.al.isAdmin(player))
                        {
                            player.sendMessage(ChatColor.RED + "WARNING: " + event.getPlayer().getName() + " just right clicked a possible command sign at " + FUtil.formatLocation(event.getClickedBlock().getLocation()));
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
}
