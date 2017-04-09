package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AntiClick extends FreedomService {

    public AntiClick(TotalFreedomMod plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }
    public static final List<Material> LISTENING_MATERIALS = Arrays.asList(
            Material.LEVER,
            Material.DAYLIGHT_DETECTOR_INVERTED,
            Material.DAYLIGHT_DETECTOR,
            Material.WOOD_BUTTON,
            Material.STONE_BUTTON);

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);

        if (!ConfigEntry.CLICK_MONITOR_ENABLED.getBoolean()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Block block = event.getClickedBlock();
            if (!LISTENING_MATERIALS.contains(block.getType())) {
                return;
            }
            if (fPlayer.incrementAndGetClickAmount() > ConfigEntry.CLICK_COUNT_LIMIT.getInteger()) {
                FUtil.bcastMsg(player.getName() + " is clicking too fast!", ChatColor.RED);
                plugin.ae.autoEject(player, "Clicking too fast can crash the server which is not allowed.");
                FLog.info(player.getName() + " is clicking too fast, they are most likely trying to crash the server.");
                fPlayer.resetBlockDestroyCount();

                event.setCancelled(true);
            }
        }

    }

}
