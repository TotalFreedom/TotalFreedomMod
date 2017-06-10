package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

public class Invsee extends FreedomService
{

    public Invsee(TotalFreedomMod plugin)
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
    public void onInventoryClickEvent(final InventoryClickEvent event)
    {
        Player refreshPlayer = null;
        final Inventory top = event.getView().getTopInventory();
        final InventoryType type = top.getType();
        final Player playerdata = (Player) event.getWhoClicked();
        FPlayer fPlayer = plugin.pl.getPlayer(playerdata);
        if (type == InventoryType.PLAYER && fPlayer.isInvsee())
        {
            final InventoryHolder invHolder = top.getHolder();
            if (invHolder != null && invHolder instanceof HumanEntity)
            {
                final Player invOwner = (Player) invHolder;
                final Rank recieverRank = plugin.rm.getRank(playerdata);
                final Rank playerRank = plugin.rm.getRank(invOwner);
                if (playerRank.ordinal() >= recieverRank.ordinal() || !(invOwner.isOnline()))
                {
                    event.setCancelled(true);
                    refreshPlayer = playerdata;
                }
            }
        }

        if (refreshPlayer != null)
        {
            final Player player = refreshPlayer;
            new BukkitRunnable()
            {

                @Override
                public void run()
                {
                    player.updateInventory();
                }

            }.runTaskLater(this.plugin, 20);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryCloseEvent(final InventoryCloseEvent event)
    {
        Player refreshPlayer = null;
        final Inventory top = event.getView().getTopInventory();
        final InventoryType type = top.getType();
        final Player playerdata = (Player) event.getPlayer();
        FPlayer fPlayer = plugin.pl.getPlayer(playerdata);
        if (type == InventoryType.PLAYER && fPlayer.isInvsee())
        {
            fPlayer.setInvsee(false);
            refreshPlayer = playerdata;
        }

        if (refreshPlayer != null)
        {
            final Player player = refreshPlayer;
            new BukkitRunnable()
            {

                @Override
                public void run()
                {
                    player.updateInventory();
                }

            }.runTaskLater(this.plugin, 20);
        }
    }

}
