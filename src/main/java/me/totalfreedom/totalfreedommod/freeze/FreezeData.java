package me.totalfreedom.totalfreedommod.freeze;

import lombok.Getter;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.player.FPlayer;

import static me.totalfreedom.totalfreedommod.TotalFreedomMod.plugin;
import static me.totalfreedom.totalfreedommod.player.FPlayer.AUTO_PURGE_TICKS;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class FreezeData
{

    private final FPlayer fPlayer;
    //
    @Getter
    private Location location = null;
    private BukkitTask unfreeze = null;

    public FreezeData(FPlayer fPlayer)
    {
        this.fPlayer = fPlayer;
    }

    public boolean isFrozen()
    {
        return unfreeze != null;
    }

    public void setFrozen(boolean freeze)
    {
        final Player player = fPlayer.getPlayer();
        if (player == null)
        {
            FLog.info("Could not freeze " + player.getName() + ". Player not online!");
            return;
        }

        FUtil.cancel(unfreeze);
        unfreeze = null;
        location = null;

        if (!freeze)
        {
            if (fPlayer.getPlayer().getGameMode() != GameMode.CREATIVE)
            {
                FUtil.setFlying(player, false);
            }

            return;
        }

        location = player.getLocation(); // Blockify location
        FUtil.setFlying(player, true); // Avoid infinite falling

        unfreeze = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!plugin().al.isAdminImpostor(player) && plugin().pv.isPlayerImpostor(player))
                {
                    FUtil.adminAction("TotalFreedom", "Unfreezing " + player.getName(), false);
                    setFrozen(false);
                }
            }

        }.runTaskLater(plugin(), AUTO_PURGE_TICKS);
    }

}
