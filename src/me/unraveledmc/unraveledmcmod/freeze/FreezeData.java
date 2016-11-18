package me.unraveledmc.unraveledmcmod.freeze;

import lombok.Getter;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.player.FPlayer;
import static me.unraveledmc.unraveledmcmod.player.FPlayer.AUTO_PURGE_TICKS;
import me.unraveledmc.unraveledmcmod.util.FLog;
import me.unraveledmc.unraveledmcmod.util.FUtil;
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
            FLog.info("Could not freeze " + fPlayer.getName() + ". Player not online!");
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

        if (fPlayer.getPlugin().al.isAdminImpostor(player))
        {
            return; // Don't run unfreeze task for impostors
        }

        unfreeze = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                FUtil.adminAction(ConfigEntry.SERVER_NAME.getString(), "Unfreezing " + player.getName(), false);
                setFrozen(false);
            }

        }.runTaskLater(fPlayer.getPlugin(), AUTO_PURGE_TICKS);
    }

}
