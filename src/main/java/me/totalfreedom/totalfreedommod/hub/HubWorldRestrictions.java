package me.totalfreedom.totalfreedommod.hub;

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

public class HubWorldRestrictions extends FreedomService
{
    
    public final List<String> ALLOWED_COMMANDS = Arrays.asList(
           "list", "opall", "gmc", "gms", "gma", "gmsp", "purgeall", "stfu", "tempban", "gtfo", "noob", "flatlands", "adminworld", "masterbuilderworld", "world", "nether", "spawn", "tpo", "tp", "expel", "item", "i", "give", "adminchat", "adventure", "creative", "survival", "spectator", "say", "blockcmd", "blockpvp", "blockredstone", "stoplag", "halt-activity", "nickclean", "nick", "nicknyan", "vanish", "verify", "verifynoadmin", "co", "coreprotect", "core", "mobpurge", "logs", "links", "vote", "o", "linkdiscord");

    public HubWorldRestrictions(TotalFreedomMod plugin)
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
        if (!FUtil.isExecutive(player.getName()) && player.getWorld().equals(plugin.wm.hubworld.getWorld()))
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
            player.sendMessage(ChatColor.RED + "Only Executives can do this in the Hub World!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            player.sendMessage(ChatColor.RED + "Only Executives can do this in the Hub World!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            player.sendMessage(ChatColor.RED + "Only Executives can do this in the Hub World!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        final Player player = event.getPlayer();

        if (doRestrict(player))
        {
            player.sendMessage(ChatColor.RED + "Only Executives can do this in the Hub World!");
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
                player.sendMessage(ChatColor.RED + "Only Executives can do this in the Hub World!");
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
            String command = event.getMessage().split("\\s+")[0].substring(1, event.getMessage().split("\\s+")[0].length()).toLowerCase();

            if (ALLOWED_COMMANDS.contains(command))
            {
                event.setCancelled(false);
            }
            else if (command.startsWith(""))
            {
                player.sendMessage(ChatColor.RED + "Only Executives are allowed to execute commands in the Hub World!");
                event.setCancelled(true);
            }
        }
    }
}
