package me.StevenLawson.TotalFreedomMod;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import java.io.File;

public class AutoOP implements Listener {

    File names = new File(TotalFreedomMod.getPlugin().getDataFolder(), "blockednames.yml");
    FileConfiguration config = YamlConfiguration.loadConfiguration(names);
    // We now have no use of the command /opall, yay!
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.getName();
        for (String s : config.getStringList("unallowednames")) {
            if (!p.getName().equals(s) && !p.isOp()) {
                p.setOp(true);
            }
        }
    }
}