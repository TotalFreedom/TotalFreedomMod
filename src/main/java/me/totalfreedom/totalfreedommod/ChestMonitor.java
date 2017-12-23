package me.totalfreedom.totalfreedommod;

import org.bukkit.event.EventPriority;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import java.text.DecimalFormat;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class ChestMonitor extends FreedomService
{

    DecimalFormat df;

    public ChestMonitor(TotalFreedomMod plugin)
    {
        super(plugin);
        this.df = new DecimalFormat("#");
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    public String GetMaterial(final int id)
    {
        return String.valueOf(Material.getMaterial(id));
    }

    @EventHandler(priority = EventPriority.LOW)
    public final void onChestMove(final InventoryClickEvent event)
    {
        if (plugin.al.isAdmin(event.getWhoClicked()))
        {
            return;
        }
        final Inventory top = event.getView().getTopInventory();
        final Inventory bottom = event.getView().getBottomInventory();
        if (top.getType() == InventoryType.CHEST && bottom.getType() == InventoryType.PLAYER && event.getCurrentItem() != null && event.getCurrentItem().getTypeId() != 0)
        {
            final Player p = (Player) event.getWhoClicked();
            final Location loc = p.getLocation();
            final int item = event.getCurrentItem().getTypeId();
            final int amount = event.getCurrentItem().getAmount();
            for (Player player : server.getOnlinePlayers())
            {
                if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).isChestMonitorEnabled())
                {
                    FUtil.playerMsg(player, p.getName() + " Moved in a chest with " + amount + " " + this.GetMaterial(item) + " [" + this.df.format(loc.getX()) + ", " + this.df.format(loc.getY()) + ", " + this.df.format(loc.getZ()) + "] at the world '" + loc.getWorld().getName() + "'.");
                }
            }
        }
        if (top.getType() == InventoryType.DISPENSER && bottom.getType() == InventoryType.PLAYER && event.getCurrentItem() != null && event.getCurrentItem().getTypeId() != 0)
        {
            final Player p2 = (Player) event.getWhoClicked();
            final Location loc = p2.getLocation();
            final int item = event.getCurrentItem().getTypeId();
            final int amount = event.getCurrentItem().getAmount();
            for (Player player : server.getOnlinePlayers())
            {
                if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).isChestMonitorEnabled())
                {
                    FUtil.playerMsg(player, p2.getName() + " Moved in a dispenser with " + amount + " " + this.GetMaterial(item) + " [" + this.df.format(loc.getX()) + ", " + this.df.format(loc.getY()) + ", " + this.df.format(loc.getZ()) + "] at the world '" + loc.getWorld().getName() + "'.");
                }

            }
        }
        if (top.getType() == InventoryType.HOPPER && bottom.getType() == InventoryType.PLAYER && event.getCurrentItem() != null && event.getCurrentItem().getTypeId() != 0)
        {
            final Player p2 = (Player) event.getWhoClicked();
            final Location loc = p2.getLocation();
            final int item = event.getCurrentItem().getTypeId();
            final int amount = event.getCurrentItem().getAmount();
            for (Player player : server.getOnlinePlayers())
            {
                if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).isChestMonitorEnabled())
                {
                    FUtil.playerMsg(player, p2.getName() + " Moved in a hopper with " + amount + " " + this.GetMaterial(item) + " [" + this.df.format(loc.getX()) + ", " + this.df.format(loc.getY()) + ", " + this.df.format(loc.getZ()) + "] at the world '" + loc.getWorld().getName() + "'.");
                }

            }
        }
        if (top.getType() == InventoryType.DROPPER && bottom.getType() == InventoryType.PLAYER && event.getCurrentItem() != null && event.getCurrentItem().getTypeId() != 0)
        {
            final Player p2 = (Player) event.getWhoClicked();
            final Location loc = p2.getLocation();
            final int item = event.getCurrentItem().getTypeId();
            final int amount = event.getCurrentItem().getAmount();
            for (Player player : server.getOnlinePlayers())
            {
                if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).isChestMonitorEnabled())
                {
                    FUtil.playerMsg(player, p2.getName() + " Moved in a dropper with " + amount + " " + this.GetMaterial(item) + " [" + this.df.format(loc.getX()) + ", " + this.df.format(loc.getY()) + ", " + this.df.format(loc.getZ()) + "] at the world '" + loc.getWorld().getName() + "'.");
                }

            }

        }
        if (top.getType() == InventoryType.SHULKER_BOX && bottom.getType() == InventoryType.PLAYER && event.getCurrentItem() != null && event.getCurrentItem().getTypeId() != 0)
        {
            final Player p2 = (Player) event.getWhoClicked();
            final Location loc = p2.getLocation();
            final int item = event.getCurrentItem().getTypeId();
            final int amount = event.getCurrentItem().getAmount();
            for (Player player : server.getOnlinePlayers())
            {
                if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).isChestMonitorEnabled())
                {
                    FUtil.playerMsg(player, p2.getName() + " Moved in a shulker box with " + amount + " " + this.GetMaterial(item) + " [" + this.df.format(loc.getX()) + ", " + this.df.format(loc.getY()) + ", " + this.df.format(loc.getZ()) + "] at the world '" + loc.getWorld().getName() + "'.");
                }

            }

        }
        if (top.getType() == InventoryType.ENDER_CHEST && bottom.getType() == InventoryType.PLAYER && event.getCurrentItem() != null && event.getCurrentItem().getTypeId() != 0)
        {
            final Player p2 = (Player) event.getWhoClicked();
            final Location loc = p2.getLocation();
            final int item = event.getCurrentItem().getTypeId();
            final int amount = event.getCurrentItem().getAmount();
            for (Player player : server.getOnlinePlayers())
            {
                if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).isChestMonitorEnabled())
                {
                    FUtil.playerMsg(player, p2.getName() + " Moved in a ender chest with " + amount + " " + this.GetMaterial(item) + " [" + this.df.format(loc.getX()) + ", " + this.df.format(loc.getY()) + ", " + this.df.format(loc.getZ()) + "] at the world '" + loc.getWorld().getName() + "'.");
                }

            }

        }
    }
}
