package me.StevenLawson.TotalFreedomMod.Listener;

import java.util.logging.Logger;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

public class TFM_BlockListener extends BlockListener
{
    private TotalFreedomMod plugin;
    private static final Logger log = Logger.getLogger("Minecraft");

    public TFM_BlockListener(TotalFreedomMod instance)
    {
        this.plugin = instance;
    }

    @Override
    public void onBlockBurn(BlockBurnEvent event)
    {
        if (!TotalFreedomMod.allowFireSpread)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        if (!TotalFreedomMod.allowFirePlace)
        {
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (TotalFreedomMod.nukeMonitor)
        {
            Player p = event.getPlayer();
            TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);

            Location player_pos = p.getLocation();
            Location block_pos = event.getBlock().getLocation();

            boolean out_of_range = false;
            if (!player_pos.getWorld().equals(block_pos.getWorld()))
            {
                out_of_range = true;
            }
            else if (player_pos.distance(block_pos) > TotalFreedomMod.nukeMonitorRange)
            {
                out_of_range = true;
            }

            if (out_of_range)
            {
                playerdata.incrementFreecamDestroyCount();
                if (playerdata.getFreecamDestroyCount() > TotalFreedomMod.freecamTriggerCount)
                {
                    TFM_Util.bcastMsg(p.getName() + " has been flagged for possible freecam nuking.", ChatColor.RED);
                    TFM_Util.autoEject(p, "Freecam (extended range) block breaking is not permitted on this server.");
                    
                    playerdata.resetFreecamDestroyCount();
                    
                    event.setCancelled(true);
                    return;
                }
            }

            playerdata.incrementBlockDestroyCount();
            if (playerdata.getBlockDestroyCount() > TotalFreedomMod.nukeMonitorCountBreak)
            {
                TFM_Util.bcastMsg(p.getName() + " is breaking blocks too fast!", ChatColor.RED);
                TFM_Util.autoEject(p, "You are breaking blocks too fast. Nukers are not permitted on this server.");
                
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player p = event.getPlayer();

        if (TotalFreedomMod.nukeMonitor)
        {
            TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);

            Location player_pos = p.getLocation();
            Location block_pos = event.getBlock().getLocation();

            boolean out_of_range = false;
            if (!player_pos.getWorld().equals(block_pos.getWorld()))
            {
                out_of_range = true;
            }
            else if (player_pos.distance(block_pos) > TotalFreedomMod.nukeMonitorRange)
            {
                out_of_range = true;
            }

            if (out_of_range)
            {
                playerdata.incrementFreecamPlaceCount();
                if (playerdata.getFreecamPlaceCount() > TotalFreedomMod.freecamTriggerCount)
                {
                    TFM_Util.bcastMsg(p.getName() + " has been flagged for possible freecam building.", ChatColor.RED);
                    TFM_Util.autoEject(p, "Freecam (extended range) block building is not permitted on this server.");
                    
                    playerdata.resetFreecamPlaceCount();
                    
                    event.setCancelled(true);
                    return;
                }
            }

            playerdata.incrementBlockPlaceCount();
            if (playerdata.getBlockPlaceCount() > TotalFreedomMod.nukeMonitorCountPlace)
            {
                TFM_Util.bcastMsg(p.getName() + " is placing blocks too fast!", ChatColor.RED);
                TFM_Util.autoEject(p, "You are placing blocks too fast.");
                
                event.setCancelled(true);
                return;
            }
        }

        ItemStack is = new ItemStack(event.getBlockPlaced().getType(), 1, (short) 0, event.getBlockPlaced().getData());
        if (is.getType() == Material.LAVA || is.getType() == Material.STATIONARY_LAVA)
        {
            if (TotalFreedomMod.allowLavaPlace)
            {
                log.info(String.format("%s placed lava @ %s",
                        p.getName(),
                        TFM_Util.formatLocation(event.getBlock().getLocation())));

                p.getInventory().clear(p.getInventory().getHeldItemSlot());
            }
            else
            {
                int slot = p.getInventory().getHeldItemSlot();
                ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
                p.getInventory().setItem(slot, heldItem);

                p.sendMessage(ChatColor.GOLD + "LAVA NO FUN, YOU EAT COOKIE INSTEAD, NO?");

                event.setCancelled(true);
                return;
            }
        }
        else if (is.getType() == Material.WATER || is.getType() == Material.STATIONARY_WATER)
        {
            if (TotalFreedomMod.allowWaterPlace)
            {
                log.info(String.format("%s placed water @ %s",
                        p.getName(),
                        TFM_Util.formatLocation(event.getBlock().getLocation())));

                p.getInventory().clear(p.getInventory().getHeldItemSlot());
            }
            else
            {
                int slot = p.getInventory().getHeldItemSlot();
                ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
                p.getInventory().setItem(slot, heldItem);

                p.sendMessage(ChatColor.GOLD + "Does this look like a waterpark to you?");

                event.setCancelled(true);
                return;
            }
        }
        else if (is.getType() == Material.FIRE)
        {
            if (TotalFreedomMod.allowFirePlace)
            {
                log.info(String.format("%s placed fire @ %s",
                        p.getName(),
                        TFM_Util.formatLocation(event.getBlock().getLocation())));

                p.getInventory().clear(p.getInventory().getHeldItemSlot());
            }
            else
            {
                int slot = p.getInventory().getHeldItemSlot();
                ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
                p.getInventory().setItem(slot, heldItem);

                p.sendMessage(ChatColor.GOLD + "It's gettin (too) hot in here...");

                event.setCancelled(true);
                return;
            }
        }
        else if (is.getType() == Material.TNT)
        {
            if (TotalFreedomMod.allowExplosions)
            {
                log.info(String.format("%s placed TNT @ %s",
                        p.getName(),
                        TFM_Util.formatLocation(event.getBlock().getLocation())));

                p.getInventory().clear(p.getInventory().getHeldItemSlot());
            }
            else
            {
                int slot = p.getInventory().getHeldItemSlot();
                ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
                p.getInventory().setItem(slot, heldItem);

                p.sendMessage(ChatColor.GRAY + "TNT is currently disabled.");

                event.setCancelled(true);
                return;
            }
        }
    }
}
