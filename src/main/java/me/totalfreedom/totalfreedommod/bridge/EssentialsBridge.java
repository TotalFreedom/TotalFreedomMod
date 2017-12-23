package me.totalfreedom.totalfreedommod.bridge;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.inventory.InventoryHolder;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.totalfreedom.totalfreedommod.util.FUtil;
import com.earth2me.essentials.User;
import org.bukkit.plugin.Plugin;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.Bukkit;
import me.totalfreedom.totalfreedommod.command.Command_vanish;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import com.earth2me.essentials.Essentials;
import me.totalfreedom.totalfreedommod.FreedomService;

public class EssentialsBridge extends FreedomService
{

    private Essentials essentialsPlugin;

    public EssentialsBridge(final TotalFreedomMod plugin)
    {
        super(plugin);
        this.essentialsPlugin = null;
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
        Command_vanish.vanished.clear();
    }

    public Essentials getEssentialsPlugin()
    {
        if (this.essentialsPlugin == null)
        {
            try
            {
                final Plugin essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                if (essentials != null && essentials instanceof Essentials)
                {
                    this.essentialsPlugin = (Essentials) essentials;
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }
        return this.essentialsPlugin;
    }

    public User getEssentialsUser(final String username)
    {
        try
        {
            final Essentials essentials = this.getEssentialsPlugin();
            if (essentials != null)
            {
                return essentials.getUserMap().getUser(username);
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return null;
    }

    public void setNickname(final String username, final String nickname)
    {
        try
        {
            final User user = this.getEssentialsUser(username);
            if (user != null)
            {
                user.setNickname(nickname);
                user.setDisplayNick();
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    public String getNickname(final String username)
    {
        try
        {
            final User user = this.getEssentialsUser(username);
            if (user != null)
            {
                return user.getNickname();
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return null;
    }

    public long getLastActivity(final String username)
    {
        try
        {
            final User user = this.getEssentialsUser(username);
            if (user != null)
            {
                return FUtil.getField(user, "lastActivity");
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return 0L;
    }

    public void setVanished(final String username, final boolean vanished)
    {
        try
        {
            final User user = this.getEssentialsUser(username);
            if (user != null)
            {
                user.setVanished(vanished);
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClickEvent(final InventoryClickEvent event)
    {
        Player refreshPlayer = null;
        final Inventory top = event.getView().getTopInventory();
        final InventoryType type = top.getType();
        final Player playerdata = (Player) event.getWhoClicked();
        final FPlayer fPlayer = ((TotalFreedomMod) this.plugin).pl.getPlayer(playerdata);
        if (type == InventoryType.PLAYER && fPlayer.isInvSee())
        {
            final InventoryHolder invHolder = top.getHolder();
            if (invHolder != null && invHolder instanceof HumanEntity)
            {
                final Player invOwner = (Player) invHolder;
                final Rank recieverRank = ((TotalFreedomMod) this.plugin).rm.getRank(playerdata);
                final Rank playerRank = ((TotalFreedomMod) this.plugin).rm.getRank(invOwner);
                if (playerRank.ordinal() >= recieverRank.ordinal() || !invOwner.isOnline())
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
            }.runTaskLater((Plugin) this.plugin, 20L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryCloseEvent(final InventoryCloseEvent event)
    {
        Player refreshPlayer = null;
        final Inventory top = event.getView().getTopInventory();
        final InventoryType type = top.getType();
        final Player playerdata = (Player) event.getPlayer();
        final FPlayer fPlayer = ((TotalFreedomMod) this.plugin).pl.getPlayer(playerdata);
        if (type == InventoryType.PLAYER && fPlayer.isInvSee())
        {
            fPlayer.setInvSee(false);
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
            }.runTaskLater((Plugin) this.plugin, 20L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuitEvent(final PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();
        if (Command_vanish.vanished.contains(player))
        {
            Command_vanish.vanished.remove(player);
        }
    }

    public boolean isEssentialsEnabled()
    {
        try
        {
            final Essentials essentials = this.getEssentialsPlugin();
            if (essentials != null)
            {
                return essentials.isEnabled();
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return false;
    }
}
