package me.totalfreedom.totalfreedommod.fun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import javax.security.auth.login.FailedLoginException;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.shop.ShopData;
import me.totalfreedom.totalfreedommod.shop.ShopItem;
import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class ItemFun extends FreedomService
{

    public List<Player> explosivePlayers = new ArrayList<Player>();

    private final Random random = new Random();

    private final Map<String, List<String>> cooldownTracker = new HashMap<>();

    private final Map<Player, Float> orientationTracker = new HashMap<>();

    private final List<UUID> FIRE_BALL_UUIDS = new ArrayList<>();

    private void cooldown(Player player, ShopItem item, int seconds)
    {
        if (cooldownTracker.get(player.getName()) == null)
        {
            List<String> featureList = new ArrayList<>();
            featureList.add(item.getDataName());
            cooldownTracker.put(player.getName(), featureList);
        }
        else
        {
            cooldownTracker.get(player.getName()).add(item.getDataName());
        }
        BukkitTask thing = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                cooldownTracker.get(player.getName()).remove(item.getDataName());
            }
        }.runTaskLater(plugin, seconds * 20);
    }

    public boolean onCooldown(Player player, ShopItem item)
    {
        if (cooldownTracker.get(player.getName()) == null)
        {
            return false;
        }
        return cooldownTracker.get(player.getName()).contains(item.getDataName());
    }

    public ItemFun(TotalFreedomMod plugin)
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

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            return;
        }

        final Player player = event.getPlayer();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);

        switch (event.getMaterial())
        {
            case TROPICAL_FISH:
            {
                final int RADIUS_HIT = 5;
                final int STRENGTH = 4;

                if (!plugin.al.isSeniorAdmin(player))
                {
                    final StringBuilder msg = new StringBuilder();
                    final char[] chars = ("That's clownery, luv").toCharArray();
                    for (char c : chars)
                    {
                        msg.append(FUtil.randomChatColor()).append(c);
                    }
                    player.sendMessage(msg.toString());

                    player.getEquipment().getItemInMainHand().setType(Material.POTATO);
                    break;
                }

                event.setCancelled(true);
                boolean didHit = false;

                final Location playerLoc = player.getLocation();
                final Vector playerLocVec = playerLoc.toVector();

                final List<Player> players = player.getWorld().getPlayers();
                for (final Player target : players)
                {
                    if (target == player)
                    {
                        continue;
                    }

                    final Location targetPos = target.getLocation();
                    final Vector targetPosVec = targetPos.toVector();

                    try
                    {
                        if (targetPosVec.distanceSquared(playerLocVec) < (RADIUS_HIT * RADIUS_HIT))
                        {
                            FUtil.setFlying(player, false);
                            target.setVelocity(targetPosVec.subtract(playerLocVec).normalize().multiply(STRENGTH));
                            didHit = true;
                        }
                    }
                    catch (IllegalArgumentException ex)
                    {
                    }
                }

                if (didHit)
                {
                    final Sound[] sounds = Sound.values();
                    for (Sound sound : sounds)
                    {
                        if (sound.toString().contains("HIT"))
                        {
                            playerLoc.getWorld().playSound(randomOffset(playerLoc, 5.0), sound, 100.0f, randomDoubleRange(0.5, 2.0).floatValue());
                        }
                    }
                }
                break;
            }

            case CARROT:
            {
                if (!ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
                {
                    break;
                }

                if (!plugin.al.isSeniorAdmin(player))
                {
                    break;
                }

                if (player.getWorld().equals(plugin.wm.masterBuilderWorld.getWorld()) && plugin.mbwr.doRestrict(player))
                {
                    break;
                }

                Location location = player.getLocation().clone();

                Vector playerPosition = location.toVector().add(new Vector(0.0, 1.65, 0.0));
                Vector playerDirection = location.getDirection().normalize();

                double distance = 150.0;
                Block targetBlock = DepreciationAggregator.getTargetBlock(player, null, Math.round((float)distance));
                if (targetBlock != null)
                {
                    distance = location.distance(targetBlock.getLocation());
                }

                final List<Block> affected = new ArrayList<>();

                Block lastBlock = null;
                for (double offset = 0.0; offset <= distance; offset += (distance / 25.0))
                {
                    Block block = playerPosition.clone().add(playerDirection.clone().multiply(offset)).toLocation(player.getWorld()).getBlock();

                    if (!block.equals(lastBlock))
                    {
                        if (block.isEmpty())
                        {
                            affected.add(block);
                            block.setType(Material.TNT);
                        }
                        else
                        {
                            break;
                        }
                    }

                    lastBlock = block;
                }

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        for (Block tntBlock : affected)
                        {
                            TNTPrimed tnt = tntBlock.getWorld().spawn(tntBlock.getLocation(), TNTPrimed.class);
                            tnt.setFuseTicks(5);
                            tntBlock.setType(Material.AIR);
                        }
                    }
                }.runTaskLater(plugin, 30L);

                event.setCancelled(true);
                break;
            }

            case BONE:
            {
                if (!fPlayer.mobThrowerEnabled())
                {
                    break;
                }

                Location player_pos = player.getLocation();
                Vector direction = player_pos.getDirection().normalize();

                LivingEntity rezzed_mob = (LivingEntity)player.getWorld().spawnEntity(player_pos.add(direction.multiply(2.0)), fPlayer.mobThrowerCreature());
                rezzed_mob.setVelocity(direction.multiply(fPlayer.mobThrowerSpeed()));
                fPlayer.enqueueMob(rezzed_mob);

                event.setCancelled(true);
                break;
            }

            case GUNPOWDER:
            {
                if (!fPlayer.isMP44Armed())
                {
                    break;
                }

                event.setCancelled(true);

                if (fPlayer.toggleMP44Firing())
                {
                    fPlayer.startArrowShooter(plugin);
                }
                else
                {
                    fPlayer.stopArrowShooter();
                }
                break;
            }

            case BLAZE_ROD:
            {
                if (!plugin.sh.isRealItem(plugin.sh.getData(player), ShopItem.LIGHTNING_ROD, player.getInventory().getItemInMainHand(), plugin.sh.getLightningRod()))
                {
                    break;
                }

                if (onCooldown(player, ShopItem.LIGHTNING_ROD))
                {
                    player.sendMessage(ChatColor.RED + "You're are currently on a cooldown for 10 seconds.");
                    break;
                }

                event.setCancelled(true);
                Block targetBlock = player.getTargetBlock(null, 20);

                for (int i = 0; i < 5; i++)
                {
                    player.getWorld().strikeLightning(targetBlock.getLocation());
                }
                cooldown(player, ShopItem.LIGHTNING_ROD, 10);
                break;
            }

            case FIRE_CHARGE:
            {
                if (!plugin.sh.isRealItem(plugin.sh.getData(player), ShopItem.FIRE_BALL, player.getInventory().getItemInMainHand(), plugin.sh.getFireBall()))
                {
                    break;
                }

                if (onCooldown(player, ShopItem.FIRE_BALL))
                {
                    player.sendMessage(ChatColor.RED + "You're are currently on a cooldown for 5 seconds.");
                    break;
                }

                event.setCancelled(true);
                Entity fireball = player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREBALL);
                FIRE_BALL_UUIDS.add(fireball.getUniqueId());
                fireball.setVelocity(player.getLocation().getDirection().multiply(2));
                cooldown(player, ShopItem.FIRE_BALL, 5);
                break;
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event)
    {
        Projectile entity = event.getEntity();
        if (entity instanceof EnderPearl && entity.getShooter() instanceof Player)
        {
            Player player = (Player)entity.getShooter();
            if (plugin.sh.isRealItem(plugin.sh.getData(player), ShopItem.RIDEABLE_PEARL, player.getInventory().getItemInMainHand(), plugin.sh.getRideablePearl()))
            {
                entity.addPassenger(player);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event)
    {
        Entity entity = event.getEntity();
        Arrow arrow = null;
        if (entity instanceof Arrow)
        {
            arrow = (Arrow)entity;
        }
        if (arrow != null && (arrow.getShooter() instanceof Player))
        {
            if (explosivePlayers.contains((Player)arrow.getShooter()))
            {
                arrow.getLocation().getWorld().createExplosion(arrow.getLocation().getX(), arrow.getLocation().getY(), arrow.getLocation().getZ(), ConfigEntry.EXPLOSIVE_RADIUS.getDouble().floatValue(), false, ConfigEntry.ALLOW_EXPLOSIONS.getBoolean());
                arrow.remove();
            }
        }

        if (entity instanceof Fireball)
        {
            if (FIRE_BALL_UUIDS.contains(entity.getUniqueId()))
            {
                FIRE_BALL_UUIDS.remove(entity.getUniqueId());
                Firework firework = (Firework)entity.getWorld().spawnEntity(entity.getLocation(), EntityType.FIREWORK);
                firework.setSilent(true);
                FireworkMeta meta = firework.getFireworkMeta();
                FireworkEffect explosionEffect = FireworkEffect.builder().withColor(Color.ORANGE).withFade(Color.YELLOW).with(FireworkEffect.Type.BALL_LARGE).trail(true).build();
                meta.addEffect(explosionEffect);
                meta.setPower(0);
                firework.setFireworkMeta(meta);
                entity.remove();
                firework.detonate();
                entity.getWorld().playSound(firework.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 10f, 1f);
            }
        }
    }

    private Location randomOffset(Location a, double magnitude)
    {
        return a.clone().add(randomDoubleRange(-1.0, 1.0) * magnitude, randomDoubleRange(-1.0, 1.0) * magnitude, randomDoubleRange(-1.0, 1.0) * magnitude);
    }

    private Double randomDoubleRange(double min, double max)
    {
        return min + (random.nextDouble() * ((max - min) + 1.0));
    }

    @EventHandler
    public void onFish(PlayerFishEvent event)
    {
        Player player = event.getPlayer();
        ShopData sd = plugin.sh.getData(player);
        PlayerInventory inv = event.getPlayer().getInventory();
        ItemStack rod = inv.getItemInMainHand();
        if (plugin.sh.isRealItem(plugin.sh.getData(player), ShopItem.GRAPPLING_HOOK, player.getInventory().getItemInMainHand(), plugin.sh.getGrapplingHook()))
        {
            if (event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.IN_GROUND)
            {
                double orientation = player.getLocation().getYaw();
                if (orientationTracker.containsKey(player))
                {
                    orientation = orientationTracker.get(player);
                }
                if (orientation < 0.0)
                {
                    orientation += 360;
                }
                int speed = 5;
                if (player.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
                {
                    speed = 15;
                }
                double xVel = 0;
                double yVel = 1;
                double zVel = 0;
                if (orientation >= 0.0 && orientation < 22.5)
                {
                    zVel = speed;
                }
                else if (orientation >= 22.5 && orientation < 67.5)
                {
                    xVel = -(speed / 2.0);
                    zVel = speed / 2.0;
                }
                else if (orientation >= 67.5 && orientation < 112.5)
                {
                    xVel = -speed;
                }
                else if (orientation >= 112.5 && orientation < 157.5)
                {
                    xVel = -(speed / 2.0);
                    zVel = -(speed / 2.0);
                }
                else if (orientation >= 157.5 && orientation < 202.5)
                {
                    zVel = -speed;
                }
                else if (orientation >= 202.5 && orientation < 247.5)
                {
                    xVel = speed / 2.0;
                    zVel = -(speed / 2.0);
                }
                else if (orientation >= 247.5 && orientation < 292.5)
                {
                    xVel = speed;
                }
                else if (orientation >= 292.5 && orientation < 337.5)
                {
                    xVel = speed / 2.0;
                    zVel = speed / 2.0;
                }
                else if (orientation >= 337.5 && orientation < 360.0)
                {
                    zVel = speed;
                }
                player.setVelocity(new Vector(xVel, yVel, zVel));
            }

            if (event.getState() == PlayerFishEvent.State.FISHING)
            {
                orientationTracker.put(player, player.getLocation().getYaw());
            }
            else
            {
                orientationTracker.remove(player);
            }
        }
    }

}
