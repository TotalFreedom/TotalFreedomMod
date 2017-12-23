package me.totalfreedom.totalfreedommod;

import java.text.DecimalFormat;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

public class PotionMonitorer extends FreedomService
{

    DecimalFormat df;

    public PotionMonitorer(TotalFreedomMod plugin)
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
    public void LingeringPotionSplashEvent(LingeringPotionSplashEvent event)
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
        final int dropeditem = event.getEntity().getItem().getTypeId();
        final Location loc = player.getLocation();

        for (Player player2 : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player2) && plugin.pl.getPlayer(player2).isPotionMonitorEnabled())
            {
                FUtil.playerMsg(player2, player.getName() + " Splashed " + event.getEntity().getItem().getAmount() + " " + this.GetMaterial(dropeditem) + " at [" + this.df.format(loc.getX()) + ", " + this.df.format(loc.getY()) + ", " + this.df.format(loc.getZ()) + "] at the world '" + loc.getWorld().getName() + "'.");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PotionSplashEvent(PotionSplashEvent event)
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
        final int dropeditem = event.getPotion().getItem().getTypeId();
        final Location loc = player.getLocation();

        for (Player player2 : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player2) && plugin.pl.getPlayer(player2).isPotionMonitorEnabled())
            {
                FUtil.playerMsg(player2, player.getName() + " Splashed " + event.getPotion().getItem().getAmount() + " " + this.GetMaterial(dropeditem) + " at [" + this.df.format(loc.getX()) + ", " + this.df.format(loc.getY()) + ", " + this.df.format(loc.getZ()) + "] at the world '" + loc.getWorld().getName() + "'.");
            }
        }
    }
}
