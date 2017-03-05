package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSpy extends FreedomService
{

    public CommandSpy(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        for (Player player : this.server.getOnlinePlayers())
        {
          Rank recieverRank = ((TotalFreedomMod)this.plugin).rm.getRank(player);
          Rank playerRank = ((TotalFreedomMod)this.plugin).rm.getRank(event.getPlayer());
          if ((playerRank.ordinal() > recieverRank.ordinal()) || (player.equals(event.getPlayer()))) {
              return;
          }
        if ((((TotalFreedomMod)this.plugin).al.isAdmin(player)) && (((TotalFreedomMod)this.plugin).pl.getPlayer(player).cmdspyEnabled())) {
            FUtil.playerMsg(player, event.getPlayer().getName() + ": " + event.getMessage());
      }
    }
  }
}
