package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSpy extends FreedomService {

    public CommandSpy(TotalFreedomMod plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (plugin.al.isAdmin(event.getPlayer())) {
            return;
        }

        for (Player player : server.getOnlinePlayers()) {
            if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).cmdspyEnabled()) {
                FUtil.playerMsg(player, event.getPlayer().getName() + ": " + event.getMessage());
            }
        }
    }

}
