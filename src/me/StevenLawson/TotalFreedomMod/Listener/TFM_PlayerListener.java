package me.StevenLawson.TotalFreedomMod.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TFM_PlayerListener implements Listener
{
    private static final List<String> BLOCKED_MUTED_CMDS = Arrays.asList(StringUtils.split("say,me,msg,m,tell,r,reply,mail,email", ","));
    private static final int MSG_PER_HEARTBEAT = 10;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        switch (event.getAction())
        {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
            {
                switch (event.getMaterial())
                {
                    case WATER_BUCKET:
                    {
                        if (!TotalFreedomMod.allowWaterPlace)
                        {
                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                            player.sendMessage(ChatColor.GRAY + "Water buckets are currently disabled.");
                            event.setCancelled(true);
                        }
                        break;
                    }
                    case LAVA_BUCKET:
                    {
                        if (!TotalFreedomMod.allowLavaPlace)
                        {
                            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                            player.sendMessage(ChatColor.GRAY + "Lava buckets are currently disabled.");
                            event.setCancelled(true);
                        }
                        break;
                    }
                    case EXPLOSIVE_MINECART:
                    {
                        if (!TotalFreedomMod.allowTntMinecarts)
                        {
                            player.getInventory().clear(player.getInventory().getHeldItemSlot());
                            player.sendMessage(ChatColor.GRAY + "TNT minecarts are currently disabled.");
                            event.setCancelled(true);
                        }
                        break;
                    }
                }
                break;
            }
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
            {
                switch (event.getMaterial())
                {
                    case STICK:
                    {
                        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                        if (playerdata.mobThrowerEnabled())
                        {
                            Location player_pos = player.getLocation();
                            Vector direction = player_pos.getDirection().normalize();

                            LivingEntity rezzed_mob = (LivingEntity) player.getWorld().spawnEntity(player_pos.add(direction.multiply(2.0)), playerdata.mobThrowerCreature());
                            rezzed_mob.setVelocity(direction.multiply(playerdata.mobThrowerSpeed()));
                            playerdata.enqueueMob(rezzed_mob);

                            event.setCancelled(true);
                        }
                        break;
                    }
                    case SULPHUR:
                    {
                        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                        if (playerdata.isMP44Armed())
                        {
                            if (playerdata.toggleMP44Firing())
                            {
                                playerdata.startArrowShooter(TotalFreedomMod.plugin);
                            }
                            else
                            {
                                playerdata.stopArrowShooter();
                            }

                            event.setCancelled(true);
                        }
                        break;
                    }
                    case BLAZE_ROD:
                    {
                        if (TotalFreedomMod.allowExplosions)
                        {
                            if (TFM_SuperadminList.isSeniorAdmin(player, true))
                            {
                                Block targetBlock;

                                if (event.getAction().equals(Action.LEFT_CLICK_AIR))
                                {
                                    targetBlock = player.getTargetBlock(null, 120);
                                }
                                else
                                {
                                    targetBlock = event.getClickedBlock();
                                }

                                if (targetBlock != null)
                                {
                                    player.getWorld().createExplosion(targetBlock.getLocation(), 4F, true);
                                    player.getWorld().strikeLightning(targetBlock.getLocation());
                                }
                                else
                                {
                                    player.sendMessage("Can't resolve target block.");
                                }

                                event.setCancelled(true);
                            }
                        }
                        break;
                    }
                    case CARROT:
                    {
                        if (TotalFreedomMod.allowExplosions)
                        {
                            if (TFM_SuperadminList.isSeniorAdmin(player, true))
                            {
                                Location player_location = player.getLocation().clone();

                                Vector player_pos = player_location.toVector().add(new Vector(0.0, 1.65, 0.0));
                                Vector player_dir = player_location.getDirection().normalize();

                                double distance = 150.0;
                                Block targetBlock = player.getTargetBlock(null, Math.round((float) distance));
                                if (targetBlock != null)
                                {
                                    distance = player_location.distance(targetBlock.getLocation());
                                }

                                final List<Block> affected = new ArrayList<Block>();

                                Block last_block = null;
                                for (double offset = 0.0; offset <= distance; offset += (distance / 25.0))
                                {
                                    Block test_block = player_pos.clone().add(player_dir.clone().multiply(offset)).toLocation(player.getWorld()).getBlock();

                                    if (!test_block.equals(last_block))
                                    {
                                        if (test_block.isEmpty())
                                        {
                                            affected.add(test_block);
                                            test_block.setType(Material.TNT);
                                        }
                                        else
                                        {
                                            break;
                                        }
                                    }

                                    last_block = test_block;
                                }

                                new BukkitRunnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        for (Block tnt_block : affected)
                                        {
                                            TNTPrimed tnt_primed = tnt_block.getWorld().spawn(tnt_block.getLocation(), TNTPrimed.class);
                                            tnt_primed.setFuseTicks(5);
                                            tnt_block.setType(Material.AIR);
                                        }
                                    }
                                }.runTaskLater(TotalFreedomMod.plugin, 30L);

                                event.setCancelled(true);
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        TFM_AdminWorld.getInstance().validateMovement(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        try
        {
            if (from.getWorld() == to.getWorld() && from.distanceSquared(to) < (0.0001 * 0.0001))
            {
                // If player just rotated, but didn't move, don't process this event.
                return;
            }
        }
        catch (IllegalArgumentException ex)
        {
        }

        if (!TFM_AdminWorld.getInstance().validateMovement(event))
        {
            return;
        }

        Player player = event.getPlayer();
        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);

        for (Entry<Player, Double> fuckoff : TotalFreedomMod.fuckoffEnabledFor.entrySet())
        {
            Player fuckoff_player = fuckoff.getKey();

            if (fuckoff_player.equals(player) || !fuckoff_player.isOnline())
            {
                continue;
            }

            double fuckoff_range = fuckoff.getValue().doubleValue();

            Location mover_pos = player.getLocation();
            Location fuckoff_pos = fuckoff_player.getLocation();

            double distanceSquared;
            try
            {
                distanceSquared = mover_pos.distanceSquared(fuckoff_pos);
            }
            catch (IllegalArgumentException ex)
            {
                continue;
            }

            if (distanceSquared < (fuckoff_range * fuckoff_range))
            {
                event.setTo(fuckoff_pos.clone().add(mover_pos.subtract(fuckoff_pos).toVector().normalize().multiply(fuckoff_range * 1.1)));
                break;
            }
        }

        boolean do_freeze = false;
        if (TotalFreedomMod.allPlayersFrozen)
        {
            if (!TFM_SuperadminList.isUserSuperadmin(player))
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
            Location freezeTo = to.clone();

            freezeTo.setX(from.getX());
            freezeTo.setY(from.getY());
            freezeTo.setZ(from.getZ());

            event.setTo(freezeTo);
        }

        if (playerdata.isCaged())
        {
            Location targetPos = player.getLocation().add(0, 1, 0);

            boolean out_of_cage;
            if (!targetPos.getWorld().equals(playerdata.getCagePos().getWorld()))
            {
                out_of_cage = true;
            }
            else
            {
                out_of_cage = targetPos.distanceSquared(playerdata.getCagePos()) > (2.5 * 2.5);
            }

            if (out_of_cage)
            {
                playerdata.setCaged(true, targetPos, playerdata.getCageMaterial(TFM_PlayerData.CageLayer.OUTER), playerdata.getCageMaterial(TFM_PlayerData.CageLayer.INNER));
                playerdata.regenerateHistory();
                playerdata.clearHistory();
                TFM_Util.buildHistory(targetPos, 2, playerdata);
                TFM_Util.generateCube(targetPos, 2, playerdata.getCageMaterial(TFM_PlayerData.CageLayer.OUTER));
                TFM_Util.generateCube(targetPos, 1, playerdata.getCageMaterial(TFM_PlayerData.CageLayer.INNER));
            }
        }

        if (playerdata.isOrbiting())
        {
            if (player.getVelocity().length() < playerdata.orbitStrength() * (2.0 / 3.0))
            {
                player.setVelocity(new Vector(0, playerdata.orbitStrength(), 0));
            }
        }

        if (TFM_Jumppads.getInstance().getMode().isOn())
        {
            TFM_Jumppads.getInstance().PlayerMoveEvent(event);
        }

        if (TotalFreedomMod.landminesEnabled && TotalFreedomMod.allowExplosions)
        {
            Iterator<TFM_LandmineData> landmines = TFM_LandmineData.landmines.iterator();
            while (landmines.hasNext())
            {
                TFM_LandmineData landmine = landmines.next();

                Location landmine_pos = landmine.landmine_pos;
                if (landmine_pos.getBlock().getType() != Material.TNT)
                {
                    landmines.remove();
                    continue;
                }

                if (!landmine.player.equals(player))
                {
                    if (player.getWorld().equals(landmine_pos.getWorld()))
                    {
                        if (player.getLocation().distanceSquared(landmine_pos) <= (landmine.radius * landmine.radius))
                        {
                            landmine.landmine_pos.getBlock().setType(Material.AIR);

                            TNTPrimed tnt1 = landmine_pos.getWorld().spawn(landmine_pos, TNTPrimed.class);
                            tnt1.setFuseTicks(40);
                            tnt1.setPassenger(player);
                            tnt1.setVelocity(new Vector(0.0, 2.0, 0.0));

                            TNTPrimed tnt2 = landmine_pos.getWorld().spawn(player.getLocation(), TNTPrimed.class);
                            tnt2.setFuseTicks(1);

                            player.setGameMode(GameMode.SURVIVAL);
                            landmines.remove();
                        }
                    }
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeavesDecay(LeavesDecayEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        try
        {
            final Player player = event.getPlayer();
            String message = event.getMessage().trim();

            TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);

            // Check for spam
            Long lastRan = TFM_Heartbeat.getLastRan();
            if (lastRan == null || lastRan + TotalFreedomMod.HEARTBEAT_RATE * 1000L < System.currentTimeMillis())
            {
                //TFM_Log.warning("Heartbeat service timeout - can't check block place/break rates.");
            }
            else
            {
                if (playerdata.incrementAndGetMsgCount() > MSG_PER_HEARTBEAT)
                {
                    TFM_Util.bcastMsg(player.getName() + " was automatically kicked for spamming chat.", ChatColor.RED);
                    TFM_Util.autoEject(player, "Kicked for spamming chat.");

                    playerdata.resetMsgCount();

                    event.setCancelled(true);
                    return;
                }
            }

            // Check for message repeat
            if (playerdata.getLastMessage().equalsIgnoreCase(message))
            {
                TFM_Util.playerMsg(player, "Please do not repeat messages.");
                event.setCancelled(true);
                return;
            }
            playerdata.setLastMessage(message);

            // Check for muted
            if (playerdata.isMuted())
            {
                if (!TFM_SuperadminList.isUserSuperadmin(player))
                {
                    player.sendMessage(ChatColor.RED + "You are muted, STFU!");
                    event.setCancelled(true);
                    return;
                }
                else
                {
                    playerdata.setMuted(false);
                }
            }

            // Strip color from messages
            message = ChatColor.stripColor(message);

            // Truncate messages that are too long - 100 characters is vanilla client max
            if (message.length() > 100)
            {
                message = message.substring(0, 100);
                TFM_Util.playerMsg(player, "Message was shortened because it was too long to send.");
            }

            // Check for caps
            if (message.length() >= 6)
            {
                int caps = 0;
                for (char c : message.toCharArray())
                {
                    if (Character.isUpperCase(c))
                    {
                        caps++;
                    }
                }
                if (((float) caps / (float) message.length()) > 0.65) //Compute a ratio so that longer sentences can have more caps.
                {
                    message = message.toLowerCase();
                }
            }

            // Check for adminchat
            if (playerdata.inAdminChat())
            {
                TFM_Util.adminChatMessage(player, message, false);
                event.setCancelled(true);
                return;
            }

            // Finally, set message
            event.setMessage(message);

            // Set the tag
            if (playerdata.getTag() != null)
            {
                player.setDisplayName((playerdata.getTag() + " " + player.getDisplayName().replaceAll(" ", "")));
            }

        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        String command = event.getMessage();
        Player player = event.getPlayer();

        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
        playerdata.setLastCommand(command);

        if (playerdata.incrementAndGetMsgCount() > MSG_PER_HEARTBEAT)
        {
            TFM_Util.bcastMsg(player.getName() + " was automatically kicked for spamming commands.", ChatColor.RED);
            TFM_Util.autoEject(player, "Kicked for spamming commands.");

            playerdata.resetMsgCount();

            TFM_Util.TFM_EntityWiper.wipeEntities(true, true);

            event.setCancelled(true);
            return;
        }

        if (playerdata.allCommandsBlocked())
        {
            TFM_Util.playerMsg(player, "Your commands have been blocked by an admin.", ChatColor.RED);
            event.setCancelled(true);
            return;
        }

        // Block commands if player is muted
        if (playerdata.isMuted())
        {
            if (!TFM_SuperadminList.isUserSuperadmin(player))
            {
                for (String test_command : BLOCKED_MUTED_CMDS)
                {
                    if (Pattern.compile("^/" + test_command.toLowerCase() + " ").matcher(command).find())
                    {
                        player.sendMessage(ChatColor.RED + "That command is blocked while you are muted.");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            else
            {
                playerdata.setMuted(false);
            }
        }

        if (TotalFreedomMod.preprocessLogEnabled)
        {
            TFM_Log.info(String.format("[PREPROCESS_COMMAND] %s(%s): %s", player.getName(), ChatColor.stripColor(player.getDisplayName()), command), true);
        }

        command = command.toLowerCase().trim();

        // Blocked commands
        if (TFM_CommandBlockerNew.getInstance().isCommandBlocked(command, event.getPlayer()))
        {
            // CommandBlocker handles messages and broadcasts
            event.setCancelled(true);
        }

        if (!TFM_SuperadminList.isUserSuperadmin(player))
        {
            for (Player pl : Bukkit.getOnlinePlayers())
            {
                if (TFM_SuperadminList.isUserSuperadmin(pl) && TFM_PlayerData.getPlayerData(pl).cmdspyEnabled())
                {
                    TFM_Util.playerMsg(pl, player.getName() + ": " + command);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if (TotalFreedomMod.autoEntityWipe)
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event)
    {
        Player player = event.getPlayer();
        if (TotalFreedomMod.fuckoffEnabledFor.containsKey(player))
        {
            TotalFreedomMod.fuckoffEnabledFor.remove(player);
        }
        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
        playerdata.disarmMP44();
        if (playerdata.isCaged())
        {
            playerdata.regenerateHistory();
            playerdata.clearHistory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (TotalFreedomMod.fuckoffEnabledFor.containsKey(player))
        {
            TotalFreedomMod.fuckoffEnabledFor.remove(player);
        }
        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
        playerdata.disarmMP44();
        if (playerdata.isCaged())
        {
            playerdata.regenerateHistory();
            playerdata.clearHistory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        try
        {
            final Player player = event.getPlayer();
            final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
            playerdata.setSuperadminIdVerified(null);

            TFM_UserList.getInstance(TotalFreedomMod.plugin).addUser(player);

            boolean superadmin_impostor = TFM_SuperadminList.isSuperadminImpostor(player);

            if (superadmin_impostor || TFM_SuperadminList.isUserSuperadmin(player))
            {
                TFM_Util.bcastMsg(ChatColor.AQUA + player.getName() + " is " + TFM_Util.getRank(player));

                if (superadmin_impostor)
                {
                    player.getInventory().clear();
                    player.setOp(false);
                    player.setGameMode(GameMode.SURVIVAL);
                    TFM_Util.bcastMsg("Warning: " + player.getName() + " has been flagged as an impostor!", ChatColor.RED);
                }
                else
                {
                    if (TFM_SuperadminList.verifyIdentity(player.getName(), player.getAddress().getAddress().getHostAddress()))
                    {
                        playerdata.setSuperadminIdVerified(Boolean.TRUE);

                        TFM_SuperadminList.updateLastLogin(player);
                    }
                    else
                    {
                        playerdata.setSuperadminIdVerified(Boolean.FALSE);

                        TFM_Util.bcastMsg("Warning: " + player.getName() + " is an admin, but is using a username not registered to one of their IPs.", ChatColor.RED);
                    }

                    player.setOp(true);
                }
            }

            if (TotalFreedomMod.adminOnlyMode)
            {
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        player.sendMessage(ChatColor.RED + "Server is currently closed to non-superadmins.");
                    }
                }.runTaskLater(TotalFreedomMod.plugin, 20L * 3L);
            }
        }
        catch (Throwable ex)
        {
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        TFM_ServerInterface.handlePlayerLogin(event);
    }
}
