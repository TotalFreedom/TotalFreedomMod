package me.StevenLawson.TotalFreedomMod;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class TotalFreedomModBlockListener extends BlockListener
{
    public static TotalFreedomMod plugin;
    private static final Logger log = Logger.getLogger("Minecraft");

    TotalFreedomModBlockListener(TotalFreedomMod instance)
    {
        plugin = instance;
    }

    @Override
    public void onBlockBurn(BlockBurnEvent event)
    {
        if (!plugin.allowFire)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        if (!plugin.allowFire)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event)
    {
        ItemStack is = new ItemStack(event.getBlockPlaced().getType(), 1, (short) 0, event.getBlockPlaced().getData());
        if (is.getType() == Material.LAVA || is.getType() == Material.STATIONARY_LAVA)
        {
            Player player = event.getPlayer();

            int slot = player.getInventory().getHeldItemSlot();
            ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
            player.getInventory().setItem(slot, heldItem);

            player.sendMessage(ChatColor.GOLD + "LAVA NO FUN, YOU EAT COOKIE INSTEAD, NO?");

            event.setCancelled(true);
            return;
        }
        else if (is.getType() == Material.WATER || is.getType() == Material.STATIONARY_WATER)
        {
            Player player = event.getPlayer();

            int slot = player.getInventory().getHeldItemSlot();
            ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
            player.getInventory().setItem(slot, heldItem);

            player.sendMessage(ChatColor.GOLD + "Does this look like a waterpark to you?");

            event.setCancelled(true);
            return;
        }
        else if (is.getType() == Material.TNT)
        {
            Player p = event.getPlayer();

            if (!plugin.allowExplosions)
            {
                Player player = event.getPlayer();

                int slot = player.getInventory().getHeldItemSlot();
                ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
                player.getInventory().setItem(slot, heldItem);

                player.sendMessage(ChatColor.GRAY + "TNT is currently disabled.");

                event.setCancelled(true);
                return;
            }
            else
            {
                log.info(String.format("%s placed TNT @ %s",
                        p.getName(),
                        plugin.formatLocation(event.getBlock().getLocation())));
            }
        }
    }
}
