package me.totalfreedom.totalfreedommod.shop;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class Votifier extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerVote(VotifierEvent event)
    {
        Vote vote = event.getVote();
        String name = vote.getUsername();
        int coinsPerVote =  ConfigEntry.SHOP_COINS_PER_VOTE.getInteger();
        Player player = server.getPlayer(name);
        PlayerData data = null;
        if (player != null)
        {
            data = plugin.pl.getData(player);
        }
        else
        {
            data = plugin.pl.getData(name);
        }

        if (data != null)
        {
            data.setCoins(data.getCoins() + coinsPerVote);
            data.setTotalVotes(data.getTotalVotes() + 1);
            plugin.pl.save(data);
            FUtil.bcastMsg(ChatColor.GREEN + name + ChatColor.AQUA + " has voted for us on " + ChatColor.GREEN + vote.getServiceName() + ChatColor.AQUA + "!");
        }

        if (player != null)
        {
            player.sendMessage(ChatColor.GREEN + "Thank you for voting for us! Here are " + coinsPerVote + " coins!");
        }
    }
}
