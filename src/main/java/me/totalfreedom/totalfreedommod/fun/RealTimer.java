package me.totalfreedom.totalfreedommod.fun;

import java.util.HashMap;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RealTimer extends FreedomService
{
    public RealTimer(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    private Map<Player, BukkitTask> tasks = new HashMap<>();

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        enable(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        disable(event.getPlayer());
    }

    public void enable(Player player)
    {
        VPlayer vPlayer = plugin.pv.getVerificationPlayer(player);
        if (vPlayer.isRealTime())
        {
            tasks.put(player, new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    player.setPlayerTime(FUtil.getTimeInTicks(vPlayer.getUtcOffset()), false);
                }
            }.runTaskTimer(plugin, 0L, 20));
        }
    }

    public void disable(Player player)
    {
        if (!tasks.containsKey(player))
            return;
        tasks.get(player).cancel();
        tasks.remove(player);
    }
}
