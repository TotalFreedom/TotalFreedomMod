package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.rank.Rank;
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
        // Gets everyone online
        for (Player player : server.getOnlinePlayers())
        {
            // Used for when the actual cmdspy message has to be sent
            final boolean sendMessage = plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).cmdspyEnabled();
            // Rank of everyone on the server, if they have sufficient perms they become the 'reciever'
            final Rank recieverRank = plugin.rm.getRank(player);
            // Any player executing a command falls under this.
            final Rank playerRank = plugin.rm.getRank(event.getPlayer());

            // If the rank of the reciever is less then or equal to the rank of the command executer, do not send the cmdspy message. Also check if you are the executer of the command
            if (playerRank.ordinal() >= recieverRank.ordinal() || player.equals(event.getPlayer()))
            {
                return;
            }

            // boolean sendMessage is used here
            if (sendMessage)
            {
                // Sends the cmdspy message if no return; statement has been met
                FUtil.playerMsg(player, event.getPlayer().getName() + ": " + event.getMessage());
            }
        }
    }

}
