package me.totalfreedom.totalfreedommod;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Monitors extends FreedomService
{
    @Getter
    private List<Map.Entry<ThrownPotion, Long>> allThrownPotions = new ArrayList<>();
    private Map<Player, List<ThrownPotion>> recentlyThrownPotions = new HashMap<>();
    private final List<PotionEffectType> badPotionEffects = new ArrayList<>(Arrays.asList(PotionEffectType.BLINDNESS,
            PotionEffectType.LEVITATION, PotionEffectType.CONFUSION, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.HUNGER)); // A list of all effects that count as "troll".

    @Override
    public void onStart()
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
        {
            for (Player player : recentlyThrownPotions.keySet())
            {
                if (plugin.sl.isStaff(player) && plugin.sl.getAdmin(player).getPotionSpy())
                {
                    List<ThrownPotion> playerThrownPotions = recentlyThrownPotions.get(player);
                    ThrownPotion latestThrownPotion = playerThrownPotions.get(playerThrownPotions.size() - 1); // Get most recently thrown potion for the position.
                    int potionsThrown = playerThrownPotions.size();
                    boolean trollPotions = false;

                    for (ThrownPotion potion : playerThrownPotions)
                    {
                        if (isTrollPotion(potion))
                        {
                            trollPotions = true;
                        }
                    }

                    FUtil.playerMsg(player, ChatColor.translateAlternateColorCodes('&', String.format("&8[&ePotionSpy&8] &r%s splashed %s %s at X: %s Y: %s Z: %s in the world '%s'%s.",
                            player.getName(), potionsThrown, potionsThrown == 1 ? "potion" : "potions", latestThrownPotion.getLocation().getBlockX(), latestThrownPotion.getLocation().getBlockY(), latestThrownPotion.getLocation().getBlockZ(),
                                latestThrownPotion.getWorld().getName(), trollPotions ? " &c(most likely troll potion/potions)" : "")));
                }
            }
            recentlyThrownPotions.clear();
        }, 0L, 40L);
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event)
    {
        if (event.getEntity().getShooter() instanceof Player)
        {
            ThrownPotion potion = event.getEntity();
            if (potion.getShooter() instanceof Player)
            {
                Player player = (Player)potion.getShooter();

                recentlyThrownPotions.putIfAbsent(player, new ArrayList<>());
                recentlyThrownPotions.get(player).add(potion);
                allThrownPotions.add(new AbstractMap.SimpleEntry<>(potion, System.currentTimeMillis()));

                if (recentlyThrownPotions.get(player).size() > 128)
                {
                    recentlyThrownPotions.get(player).remove(0);
                }
                if (allThrownPotions.size() > 1024)
                {
                    allThrownPotions.remove(0); // Remove the first element in the set.
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event)
    {
        if (event.getEntity().getShooter() instanceof Player)
        {
            ThrownPotion potion = event.getEntity();
            if (potion.getShooter() instanceof Player)
            {
                Player player = (Player)potion.getShooter();

                recentlyThrownPotions.putIfAbsent(player, new ArrayList<>());
                recentlyThrownPotions.get(player).add(potion);
                allThrownPotions.add(new AbstractMap.SimpleEntry<>(potion, System.currentTimeMillis()));

                if (recentlyThrownPotions.get(player).size() > 128)
                {
                    recentlyThrownPotions.get(player).remove(0);
                }
                if (allThrownPotions.size() > 1024)
                {
                    allThrownPotions.remove(0); // Remove the first element in the set.
                }
            }
        }
    }

    public List<Map.Entry<ThrownPotion, Long>> getPlayerThrownPotions(Player player)
    {
        List<Map.Entry<ThrownPotion, Long>> thrownPotions = new ArrayList<>();

        for (Map.Entry<ThrownPotion, Long> potionEntry : allThrownPotions)
        {
            ThrownPotion potion = potionEntry.getKey();
            if (potion.getShooter() != null && potion.getShooter().equals(player))
            {
                thrownPotions.add(potionEntry);
            }
        }

        return thrownPotions;
    }

    public boolean isTrollPotion(ThrownPotion potion)
    {
        int badEffectsDetected = 0;

        for (PotionEffect effect : potion.getEffects())
        {
            if (badPotionEffects.contains(effect.getType()) && effect.getAmplifier() > 2 && effect.getDuration() > 200)
            {
                badEffectsDetected++;
            }
        }

        return badEffectsDetected > 0;
    }
}
