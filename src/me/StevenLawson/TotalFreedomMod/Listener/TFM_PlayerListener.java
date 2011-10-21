package me.StevenLawson.TotalFreedomMod.Listener;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.TFM_LandmineData;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class TFM_PlayerListener extends PlayerListener
{
    private TotalFreedomMod plugin;
    private static final Logger log = Logger.getLogger("Minecraft");

    public TFM_PlayerListener(TotalFreedomMod instance)
    {
        this.plugin = instance;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Material material = event.getMaterial();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
        {
            if (material == Material.WATER_BUCKET)
            {
                player.getInventory().setItemInHand(new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GOLD + "Does this look like a waterpark to you?");
                event.setCancelled(true);
                return;
            }
            else if (material == Material.LAVA_BUCKET)
            {
                player.getInventory().setItemInHand(new ItemStack(Material.COOKIE, 1));
                player.sendMessage(ChatColor.GOLD + "LAVA NO FUN, YOU EAT COOKIE INSTEAD, NO?");
                event.setCancelled(true);
                return;
            }
        }
        else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
        {
            if (material == Material.STICK)
            {
                TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(player, plugin);
                if (playerdata.mobThrowerEnabled())
                {
                    Location player_pos = player.getLocation();
                    Vector direction = player_pos.getDirection().normalize();

                    LivingEntity rezzed_mob = player.getWorld().spawnCreature(player_pos.add(direction.multiply(2.0)), playerdata.mobThrowerCreature());
                    rezzed_mob.setVelocity(direction.multiply(playerdata.mobThrowerSpeed()));
                    playerdata.enqueueMob(rezzed_mob);

                    event.setCancelled(true);
                }
            }
            else if (material == Material.SULPHUR)
            {
                TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(player, plugin);

                if (playerdata.isMP44Armed())
                {
                    if (playerdata.toggleMP44Firing())
                    {
                        playerdata.startArrowShooter(plugin);
                    }
                    else
                    {
                        playerdata.stopArrowShooter();
                    }

                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player p = event.getPlayer();
        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p, plugin);

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
            if (playerdata.isFrozen())
            {
                do_freeze = true;
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

        if (playerdata.isCaged())
        {
            Location target_pos = p.getLocation().add(0, 1, 0);

            boolean out_of_cage = false;
            if (!target_pos.getWorld().equals(playerdata.getCagePos().getWorld()))
            {
                out_of_cage = true;
            }
            else
            {
                out_of_cage = target_pos.distance(playerdata.getCagePos()) > 2.5;
            }

            if (out_of_cage)
            {
                playerdata.setCaged(true, target_pos, playerdata.getCageMaterial(TFM_UserInfo.CageLayer.INNER), playerdata.getCageMaterial(TFM_UserInfo.CageLayer.OUTER));
                playerdata.regenerateHistory();
                playerdata.clearHistory();
                TFM_Util.buildHistory(target_pos, 2, playerdata);
                TFM_Util.generateCube(target_pos, 2, playerdata.getCageMaterial(TFM_UserInfo.CageLayer.OUTER));
                TFM_Util.generateCube(target_pos, 1, playerdata.getCageMaterial(TFM_UserInfo.CageLayer.INNER));
            }
        }

        if (playerdata.isOrbiting())
        {
            if (p.getVelocity().length() < playerdata.orbitStrength() * (2.0 / 3.0))
            {
                p.setVelocity(new Vector(0, playerdata.orbitStrength(), 0));
            }
        }
        
        Iterator<TFM_LandmineData> landmines = plugin.landmines.iterator();
        while (landmines.hasNext())
        {
            TFM_LandmineData landmine = landmines.next();
            
            if (!landmine.player.equals(p))
            {
                if (p.getWorld().equals(landmine.landmine_pos.getWorld()))
                {
                    if (p.getLocation().distance(landmine.landmine_pos) <= 2.0)
                    {
                        landmine.landmine_pos.getBlock().setType(Material.AIR);
                        TNTPrimed primed_tnt = landmine.landmine_pos.getWorld().spawn(landmine.landmine_pos, TNTPrimed.class);
                        primed_tnt.setFuseTicks(100);
                        primed_tnt.setPassenger(p);
                        primed_tnt.setVelocity(new Vector(0.0, 10.0, 0.0));
                        p.setGameMode(GameMode.SURVIVAL);
                        landmines.remove();
                    }
                }
            }
        }
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event)
    {
        Player p = event.getPlayer();

        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p, plugin);
        playerdata.incrementMsgCount();

        if (playerdata.getMsgCount() > 10)
        {
            p.setOp(false);
            p.kickPlayer("No Spamming");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("tempban %s 1m", p.getName()));
            TFM_Util.tfm_broadcastMessage(p.getName() + " was automatically kicked for spamming chat.", ChatColor.RED);
            playerdata.resetMsgCount();

            event.setCancelled(true);
            return;
        }

        String message = event.getMessage().toLowerCase();
        if (Pattern.compile("\\sbe\\s.*admin").matcher(message).find()
                || Pattern.compile("\\shave\\s.*admin").matcher(message).find())
        {
            log.info("Kicked " + p.getName() + " for being annoying.");
            p.kickPlayer("No, bitch.");
            event.setCancelled(true);
            return;
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

        if (Pattern.compile("^/stop").matcher(command).find())
        {
            if (!TFM_Util.isUserSuperadmin(player, plugin))
            {
                block_command = true;
            }
        }
        else if (Pattern.compile("^/reload").matcher(command).find())
        {
            if (!TFM_Util.isUserSuperadmin(player, plugin))
            {
                block_command = true;
            }
        }
        else if (Pattern.compile("^/time").matcher(command).find())
        {
            player.sendMessage(ChatColor.GRAY + "Server-side time changing is disabled. Please use /ptime to set your own personal time.");
            block_command = true;
        }

        if (block_command)
        {
            player.sendMessage(ChatColor.RED + "That command is prohibited.");
            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if (plugin.autoEntityWipe)
        {
            if (event.getPlayer().getWorld().getEntities().size() > 750)
            {
                event.setCancelled(true);
            }
            else
            {
                event.getItemDrop().remove();
            }
        }
    }

    @Override
    public void onPlayerKick(PlayerKickEvent event)
    {
        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(event.getPlayer(), plugin);
        playerdata.disarmMP44();
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(event.getPlayer(), plugin);
        playerdata.disarmMP44();
    }
}
