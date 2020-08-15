package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FLog;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class VanishHandler extends FreedomService
{

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        for (Player p : server.getOnlinePlayers())
        {
            if (!plugin.al.isAdmin(player) && plugin.al.isVanished(p.getName()))
            {
                player.hidePlayer(plugin, p);
            }
        }

        for (Player p : server.getOnlinePlayers())
        {
            if (!plugin.al.isAdmin(p) && plugin.al.isVanished(player.getName()))
            {
                p.hidePlayer(plugin, player);
            }
        }

        if (plugin.al.isVanished(player.getName()))
        {
            plugin.esb.setVanished(player.getName(), true);
            FLog.info(player.getName() + " joined while still vanished.");
            plugin.al.messageAllAdmins(ChatColor.YELLOW + player.getName() + " has joined silently.");
            event.setJoinMessage(null);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (!plugin.al.isVanished(player.getName()))
                    {
                        this.cancel();
                    }

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "You are hidden from other players."));
                }
            }.runTaskTimer(plugin, 0L, 4L);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        if (plugin.al.isVanished(player.getName()))
        {
            event.setQuitMessage(null);
            FLog.info(player.getName() + " left while still vanished.");
            plugin.al.messageAllAdmins(ChatColor.YELLOW + player.getName() + " has left silently.");
        }
    }
}