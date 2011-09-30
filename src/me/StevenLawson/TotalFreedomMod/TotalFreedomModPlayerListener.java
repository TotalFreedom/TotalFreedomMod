package me.StevenLawson.TotalFreedomMod;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

class TotalFreedomModPlayerListener extends PlayerListener
{
    public static TotalFreedomMod plugin;
    private static final Logger log = Logger.getLogger("Minecraft");

    TotalFreedomModPlayerListener(TotalFreedomMod instance)
    {
        plugin = instance;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (event.getMaterial() == Material.WATER_BUCKET)
            {
                Player player = event.getPlayer();

                int slot = player.getInventory().getHeldItemSlot();
                ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
                player.getInventory().setItem(slot, heldItem);

                player.sendMessage(ChatColor.GOLD + "Does this look like a waterpark to you?");

                event.setCancelled(true);
                return;
            }
            else if (event.getMaterial() == Material.LAVA_BUCKET)
            {
                Player player = event.getPlayer();

                int slot = player.getInventory().getHeldItemSlot();
                ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
                player.getInventory().setItem(slot, heldItem);

                player.sendMessage(ChatColor.GOLD + "LAVA NO FUN, YOU EAT COOKIE INSTEAD, NO?");

                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (plugin.playersFrozen)
        {
            if (plugin.isUserSuperadmin(event.getPlayer()))
            {
                return;
            }
            
            Location from = event.getFrom();
            Location to = event.getTo().clone();
            
            to.setX(from.getX());
            to.setY(from.getY());
            to.setZ(from.getZ());

            event.setTo(to);
        }
    }

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        String command = event.getMessage();
        Player player = event.getPlayer();

        if (plugin.preprocessLogEnabled)
        {
            log.info(String.format("[PREPROCESS_COMMAND] %s(%s): %s", player.getName(), ChatColor.stripColor(player.getDisplayName()), command));
        }

        command = command.toLowerCase();

        boolean block_command = false;

        if (command.matches("^/stop"))
        {
            if (!plugin.isUserSuperadmin(player))
            {
                block_command = true;
            }
        }
        else if (command.matches("^/reload"))
        {
            if (!plugin.isUserSuperadmin(player))
            {
                block_command = true;
            }
        }
        else if (command.matches("^/zeus"))
        {
            block_command = true;
        }
        else if (command.matches("^/vulcan"))
        {
            block_command = true;
        }

        if (block_command)
        {
            player.sendMessage(ChatColor.RED + "That command is prohibited.");
            event.setCancelled(true);
            return;
        }
    }
}
