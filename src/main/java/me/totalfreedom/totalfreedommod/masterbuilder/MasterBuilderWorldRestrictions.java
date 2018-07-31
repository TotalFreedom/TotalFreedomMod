package me.totalfreedom.totalfreedommod.masterbuilder;

import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MasterBuilderWorldRestrictions extends FreedomService
{

    private final List<String> BLOCKED_WORLDEDIT_COMMANDS = Arrays.asList(
            "green", "fixlava", "fixwater", "br", "brush", "tool", "mat", "range", "cs", "up", "fill", "setblock");

    public MasterBuilderWorldRestrictions(TotalFreedomMod plugin)
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

    public boolean doRestrict(Player player)
    {
        if (!plugin.mbl.isMasterBuilder(player) && !FUtil.isExecutive(player.getName()) && player.getWorld().equals(plugin.wm.masterBuilderWorld.getWorld()))
        {
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player)event.getDamager();

            if (doRestrict(player))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        final Player player = event.getPlayer();
        if (doRestrict(player))
        {
            // This is a very poor way of blocking WorldEdit commands, all the methods I know of
            // obtaining a list of a plugin's commands are returning null for world edit.
            String message = event.getMessage();
            if (message.startsWith("//") || BLOCKED_WORLDEDIT_COMMANDS.contains(message.split("\\s+")[0].replace("/", "")))
            {
                player.sendMessage(ChatColor.RED + "Only Master Builders are allowed to use this WorldEdit command in the Master Builder world.");
                event.setCancelled(true);
            }

            if (plugin.al.isAdmin(player) && !plugin.al.isSeniorAdmin(player) && message.startsWith("/co"))
            {
                player.sendMessage(ChatColor.RED + "Only Senior Admins are allowed to use CoreProtect in the Master Builder world.");
                event.setCancelled(true);
            }
        }
    }
}
