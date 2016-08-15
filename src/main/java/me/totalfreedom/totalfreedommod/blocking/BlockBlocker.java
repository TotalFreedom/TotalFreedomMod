package me.totalfreedom.totalfreedommod.blocking;

import me.totalfreedom.totalfreedommod.*;
import org.bukkit.event.block.*;
import me.totalfreedom.totalfreedommod.config.*;
import me.totalfreedom.totalfreedommod.util.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class BlockBlocker extends FreedomService
{
    public BlockBlocker(final TotalFreedomMod plugin) {
        super(plugin);
    }
    
    protected void onStart() {
    }
    
    protected void onStop() {
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        switch (event.getBlockPlaced().getType()) {
            case LAVA:
            case STATIONARY_LAVA: {
                if (ConfigEntry.ALLOW_LAVA_PLACE.getBoolean()) {
                    FLog.info(String.format("%s placed lava @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));
                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    break;
                }
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "Lava placement is currently disabled.");
                event.setCancelled(true);
                break;
            }
            case WATER:
            case STATIONARY_WATER: {
                if (ConfigEntry.ALLOW_WATER_PLACE.getBoolean()) {
                    FLog.info(String.format("%s placed water @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));
                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    break;
                }
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "Water placement is currently disabled.");
                event.setCancelled(true);
                break;
            }
            case FIRE: {
                if (ConfigEntry.ALLOW_FIRE_PLACE.getBoolean()) {
                    FLog.info(String.format("%s placed fire @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));
                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    break;
                }
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "Fire placement is currently disabled.");
                event.setCancelled(true);
                break;
            }
            case TNT: {
                if (ConfigEntry.ALLOW_EXPLOSIONS.getBoolean()) {
                    FLog.info(String.format("%s placed TNT @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));
                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    break;
                }
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GRAY + "TNT is currently disabled.");
                event.setCancelled(true);
                break;
            }
            case STRUCTURE_BLOCK:{
                player.sendMessage(ChatColor.GRAY + "Use of structure blocks are disabled.");
                event.setCancelled(true);
                break;
            }
            case WRITTEN_BOOK:{
                player.sendMessage(ChatColor.GRAY + "Use of Written Book are disabled.");
                event.setCancelled(true);
                break;
            }
        }
    }
}
