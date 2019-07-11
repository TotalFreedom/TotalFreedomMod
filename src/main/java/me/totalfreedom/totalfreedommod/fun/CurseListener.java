package me.totalfreedom.totalfreedommod.fun;

import java.util.HashMap;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import static org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

public class CurseListener extends FreedomService
{

    public HashMap<Player, Player> cursedPlayers = new HashMap<Player, Player>();

    public CurseListener(TotalFreedomMod plugin)
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
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event)
    {
        Player player = event.getPlayer();
        PlayerResourcePackStatusEvent.Status cursed = event.getStatus();
        Player cursedBy = cursedPlayers.get(player);
        if (cursed.equals(Status.ACCEPTED))
        {
            cursedBy.sendMessage(ChatColor.GREEN + "Casting curse on " + player.getName() + "...");
        }
        else if (cursed.equals(Status.DECLINED))
        {
            cursedBy.sendMessage(ChatColor.RED + "Failed to start cast on " + player.getName() + "!");
            cursedPlayers.remove(player);
        }
        else if (cursed.equals(Status.SUCCESSFULLY_LOADED))
        {
            cursedBy.sendMessage(ChatColor.GREEN + "Successfully cursed + " + player.getName() + "!");
            cursedPlayers.remove(player);
        }
        else if (cursed.equals(Status.FAILED_DOWNLOAD))
        {
            cursedBy.sendMessage(ChatColor.RED + "Failed to cast curse on " + player.getName() + "!");
            cursedPlayers.remove(player);
        }
    }

}
