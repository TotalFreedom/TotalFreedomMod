package me.totalfreedom.totalfreedommod.blocking;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.util.MaterialGroup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBlocker extends FreedomService
{

    public BlockBlocker(TotalFreedomMod plugin)
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
    public void onBlockPlace(BlockPlaceEvent event)
    {
        final Player player = event.getPlayer();

        switch (event.getBlockPlaced().getType())
        {
            case LAVA:
            {
                if (ConfigEntry.ALLOW_LAVA_PLACE.getBoolean())
                {
                    FLog.info(String.format("%s placed lava @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                    player.sendMessage(ChatColor.GRAY + "Lava placement is currently disabled.");
                    event.setCancelled(true);
                }
                break;
            }
            case WATER:
            {
                if (ConfigEntry.ALLOW_WATER_PLACE.getBoolean())
                {
                    FLog.info(String.format("%s placed water @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                    player.sendMessage(ChatColor.GRAY + "Water placement is currently disabled.");

                    event.setCancelled(true);
                }
                break;
            }
            case FIRE:
            {
                if (ConfigEntry.ALLOW_FIRE_PLACE.getBoolean())
                {
                    FLog.info(String.format("%s placed fire @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                    player.sendMessage(ChatColor.GRAY + "Fire placement is currently disabled.");
                    event.setCancelled(true);
                }
                break;
            }
            case TNT:
            {
                if (ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
                {
                    FLog.info(String.format("%s placed TNT @ %s", player.getName(), FUtil.formatLocation(event.getBlock().getLocation())));
                }
                break;
            }
            case STRUCTURE_BLOCK:
            {
                player.sendMessage(ChatColor.GRAY + "Structure blocks are disabled.");
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShulkerBoxPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlock();
        if (MaterialGroup.SHULKER_BOXES.contains(event.getBlock().getType()))
        {
            ShulkerBox shulkerBox = (ShulkerBox)block.getState();
            boolean empty = true;
            for (ItemStack itemStack : shulkerBox.getInventory().getContents())
            {
                if (itemStack != null)
                {
                    empty = false;
                    break;
                }
            }
            if (!empty)
            {
                shulkerBox.getInventory().clear();
                event.getPlayer().sendMessage(ChatColor.RED + "For security reasons, your shulker box has been emptied.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDispenserPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlock();
        if (block.getType().equals(Material.DISPENSER))
        {
            Dispenser dispenser = (Dispenser)block.getState();
            boolean empty = true;
            for (ItemStack itemStack : dispenser.getInventory().getContents())
            {
                if (itemStack != null)
                {
                    empty = false;
                    break;
                }
            }
            if (!empty)
            {
                dispenser.getInventory().clear();
                event.getPlayer().sendMessage(ChatColor.RED + "For security reasons, dispenser has been emptied.");
            }
        }
    }

}
