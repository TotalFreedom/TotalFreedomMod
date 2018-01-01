package me.totalfreedom.totalfreedommod;

import java.text.DecimalFormat;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

public class Monitors extends FreedomService
{

    private final DecimalFormat decimalFormat = new DecimalFormat("#");

    public Monitors(TotalFreedomMod plugin)
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

    public String getMaterial(final int id)
    {
        return String.valueOf(Material.getMaterial(id));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event)
    {
        ProjectileSource source = event.getEntity().getShooter();

        if (!(source instanceof Player))
        {
            return;
        }
        Player player = (Player) source;

        if (plugin.al.isAdmin((Player) event.getEntity().getShooter()))
        {
            return;
        }
        final int droppedItem = event.getEntity().getItem().getTypeId();
        final Location location = player.getLocation();

        for (Player p : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(p) && plugin.pl.getPlayer(p).isPotionMonitorEnabled())
            {
                FUtil.playerMsg(p, player.getName() + " splashed " + event.getEntity().getItem().getAmount() + " " + getMaterial(droppedItem) + " at [" + decimalFormat.format(location.getX()) + ", " + decimalFormat.format(location.getY()) + ", " + decimalFormat.format(location.getZ()) + "] in the world '" + location.getWorld().getName() + "'.");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPotionSplash(PotionSplashEvent event)
    {
        ProjectileSource source = event.getEntity().getShooter();

        if (!(source instanceof Player))
        {
            return;
        }
        Player player = (Player) source;

        if (plugin.al.isAdmin((Player) event.getEntity().getShooter()))
        {
            return;
        }
        final int droppedItem = event.getPotion().getItem().getTypeId();
        final Location location = player.getLocation();

        for (Player p : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(p) && plugin.pl.getPlayer(p).isPotionMonitorEnabled())
            {
                FUtil.playerMsg(p, player.getName() + " splashed " + event.getPotion().getItem().getAmount() + " " + getMaterial(droppedItem) + " at [" + decimalFormat.format(location.getX()) + ", " + decimalFormat.format(location.getY()) + ", " + decimalFormat.format(location.getZ()) + "] in the world '" + location.getWorld().getName() + "'.");
            }
        }
    }
}