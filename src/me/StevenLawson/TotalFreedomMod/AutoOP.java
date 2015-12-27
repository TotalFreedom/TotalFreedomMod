package me.StevenLawson.TotalFreedomMod;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class AutoOP implements Listener {

    // We now have no use of the command /opall, yay!
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if(!p.isOp()) {
            p.setOp(true);
        }
    }
}