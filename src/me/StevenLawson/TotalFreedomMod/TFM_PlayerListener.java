package me.StevenLawson.TotalFreedomMod;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

class TFM_PlayerListener extends PlayerListener
{
    private TotalFreedomMod plugin;
    private static final Logger log = Logger.getLogger("Minecraft");

    TFM_PlayerListener(TotalFreedomMod instance)
    {
        this.plugin = instance;
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
        Player p = event.getPlayer();
        TFM_UserInfo playerdata = TotalFreedomMod.userinfo.get(p);

        boolean do_freeze = false;
        if (plugin.allPlayersFrozen)
        {
            if (!TFM_Util.isUserSuperadmin(p, plugin))
            {
                do_freeze = true;
            }
        }
        else
        {
            if (playerdata != null)
            {
                if (playerdata.isFrozen())
                {
                    do_freeze = true;
                }
            }
        }

        if (do_freeze)
        {
            Location from = event.getFrom();
            Location to = event.getTo().clone();

            to.setX(from.getX());
            to.setY(from.getY());
            to.setZ(from.getZ());

            event.setTo(to);
        }
        
        if (playerdata != null)
        {
            if (playerdata.isCaged())
            {
                Location target_pos = p.getLocation().add(0, 1, 0);

                if (target_pos.distance(playerdata.getCagePos()) > 2.5)
                {
                    playerdata.setCaged(true, target_pos, playerdata.getCageMaterial(0), playerdata.getCageMaterial(1));
                    playerdata.regenerateHistory();
                    playerdata.clearHistory();
                    TFM_Util.buildHistory(target_pos, 2, playerdata);
                    TFM_Util.generateCube(target_pos, 2, playerdata.getCageMaterial(0));
                    TFM_Util.generateCube(target_pos, 1, playerdata.getCageMaterial(1));
                }
            }
        }
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event)
    {
        Player p = event.getPlayer();

        TFM_UserInfo playerdata = TotalFreedomMod.userinfo.get(p);
        if (playerdata != null)
        {
            playerdata.incrementMsgCount();

            if (playerdata.getMsgCount() > 10)
            {
                p.setOp(false);
                p.kickPlayer("No Spamming");
                TFM_Util.tfm_broadcastMessage(p.getName() + " was automatically kicked for spamming chat.", ChatColor.RED);

                event.setCancelled(true);
                return;
            }
        }
        else
        {
            playerdata = new TFM_UserInfo();
            playerdata.incrementMsgCount();
            TotalFreedomMod.userinfo.put(p, playerdata);
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

        command = command.toLowerCase().trim();

        boolean block_command = false;

        if (command.matches("^/stop"))
        {
            if (!TFM_Util.isUserSuperadmin(player, plugin))
            {
                block_command = true;
            }
        }
        else if (command.matches("^/reload"))
        {
            if (!TFM_Util.isUserSuperadmin(player, plugin))
            {
                block_command = true;
            }
        }
        
        if (block_command)
        {
            player.sendMessage(ChatColor.RED + "That command is prohibited.");
            event.setCancelled(true);
            return;
        }
    }
}
