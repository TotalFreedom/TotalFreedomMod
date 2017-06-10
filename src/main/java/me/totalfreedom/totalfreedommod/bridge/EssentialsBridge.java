package me.totalfreedom.totalfreedommod.bridge;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EssentialsBridge extends FreedomService
{

    private Essentials essentialsPlugin = null;

    public EssentialsBridge(TotalFreedomMod plugin)
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

    public Essentials getEssentialsPlugin()
    {
        if (essentialsPlugin == null)
        {
            try
            {
                final Plugin essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                if (essentials != null)
                {
                    if (essentials instanceof Essentials)
                    {
                        essentialsPlugin = (Essentials) essentials;
                    }
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }
        return essentialsPlugin;
    }

    public User getEssentialsUser(String username)
    {
        try
        {
            final Essentials essentials = getEssentialsPlugin();
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

    public void setNickname(String username, String nickname)
    {
        try
        {
            final User user = getEssentialsUser(username);
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

    public String getNickname(String username)
    {
        try
        {
            final User user = getEssentialsUser(username);
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

    public long getLastActivity(String username)
    {
        try
        {
            final User user = getEssentialsUser(username);
            if (user != null)
            {
                return FUtil.<Long>getField(user, "lastActivity"); // This is weird
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return 0L;
    }

    public boolean isEssentialsEnabled()
    {
        try
        {
            final Essentials essentials = getEssentialsPlugin();
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

    public Boolean isVanished(String username)
    {

        final User user = getEssentialsUser(username);
        if (user != null)
        {
            return user.isVanished();
        }

        return null;
    }

    public void setVanished(String username, boolean vanished)
    {

        final IUser user = getEssentialsUser(username);
        if (user != null)
        {
            user.setVanished(vanished);
        }

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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        for (Player vanishedPlayers : server.getOnlinePlayers())
        {
            final FPlayer fPlayer = plugin.pl.getPlayer(vanishedPlayers);
            if (fPlayer.isVanish())
            {
                player.hidePlayer(vanishedPlayers);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);
        if (fPlayer.isVanish())
        {

            FLog.info(player.getName() + " is no longer vanished.");
            for (Player allPlayers : server.getOnlinePlayers())
            {
                player.showPlayer(allPlayers);
            }
            plugin.esb.setVanished(player.getName(), false);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            fPlayer.setVanish(false);
        }
    }
}
