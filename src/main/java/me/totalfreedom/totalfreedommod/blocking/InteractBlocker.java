package me.totalfreedom.totalfreedommod.blocking;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractBlocker extends FreedomService
{

    public InteractBlocker(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onRightClickBell(PlayerInteractEvent event)
    {
        if (event.getClickedBlock() != null)
        {
            if (event.getClickedBlock().getType().equals(Material.BELL))
            {
                if (!ConfigEntry.ALLOW_BELLS.getBoolean())
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    private void handleRightClick(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();

        if (event.getClickedBlock() != null)
        {
            if (event.getClickedBlock().getType().equals(Material.STRUCTURE_BLOCK) || event.getClickedBlock().getType().equals(Material.JIGSAW))
            {
                event.setCancelled(true);
                event.getPlayer().closeInventory();
            }
        }

        if (Groups.SPAWN_EGGS.contains(event.getMaterial()))
        {
            player.getInventory().clear(player.getInventory().getHeldItemSlot());
            player.sendMessage(ChatColor.GRAY + "Spawn eggs are currently disabled.");
            event.setCancelled(true);
            return;
        }

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

            case TNT_MINECART:
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
}
