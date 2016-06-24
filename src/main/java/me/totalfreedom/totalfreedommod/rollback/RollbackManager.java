package me.totalfreedom.totalfreedommod.rollback;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RollbackManager extends FreedomService
{

    private static final Map<String, List<RollbackEntry>> history = Maps.newHashMap();
    private static final List<String> removeHistory = Lists.newArrayList();

    public RollbackManager(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent event)
    {
        storeEntry(event.getPlayer(), new RollbackEntry(event.getPlayer().getName(), event.getBlock(), EntryType.BLOCK_PLACE));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent event)
    {
        storeEntry(event.getPlayer(), new RollbackEntry(event.getPlayer().getName(), event.getBlock(), EntryType.BLOCK_BREAK));
    }

    private void storeEntry(Player player, RollbackEntry entry)
    {
        List<RollbackEntry> playerEntryList = getEntriesByPlayer(player.getName());

        if (playerEntryList != null)
        {
            playerEntryList.add(0, entry);
        }
    }

    // May return null
    public String findPlayer(String partial)
    {
        partial = partial.toLowerCase();

        for (String player : history.keySet())
        {
            if (player.toLowerCase().equals(partial))
            {
                return player;
            }
        }

        for (String player : history.keySet())
        {
            if (player.toLowerCase().contains(partial))
            {
                return player;
            }
        }

        return null;
    }

    public int purgeEntries()
    {
        Iterator<List<RollbackEntry>> it = history.values().iterator();
        while (it.hasNext())
        {
            List<RollbackEntry> playerEntryList = it.next();
            if (playerEntryList != null)
            {
                playerEntryList.clear();
            }
        }
        return history.size();
    }

    public int purgeEntries(String playerName)
    {
        List<RollbackEntry> playerEntryList = getEntriesByPlayer(playerName);

        if (playerEntryList == null)
        {
            return 0;
        }

        int count = playerEntryList.size();
        playerEntryList.clear();
        return count;

    }

    public boolean canRollback(String playerName)
    {
        return history.containsKey(playerName.toLowerCase()) && !history.get(playerName.toLowerCase()).isEmpty();
    }

    public boolean canUndoRollback(String playerName)
    {
        return removeHistory.contains(playerName.toLowerCase());
    }

    public int rollback(final String playerName)
    {
        final List<RollbackEntry> entries = getEntriesByPlayer(playerName);
        if (entries == null)
        {
            return 0;
        }

        int count = entries.size();
        for (RollbackEntry entry : entries)
        {
            if (entry != null)
            {
                entry.restore();
            }
        }

        if (!removeHistory.contains(playerName.toLowerCase()))
        {
            removeHistory.add(playerName.toLowerCase());
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (removeHistory.contains(playerName.toLowerCase()))
                {
                    removeHistory.remove(playerName.toLowerCase());
                    purgeEntries(playerName);
                }
            }
        }.runTaskLater(plugin, 40L * 20L);
        return count;
    }

    public int undoRollback(String playerName)
    {
        final List<RollbackEntry> entries = getEntriesByPlayer(playerName);

        if (entries == null)
        {
            return 0;
        }

        final int count = entries.size();

        final ListIterator<RollbackEntry> it = entries.listIterator(count);
        while (it.hasPrevious())
        {
            RollbackEntry entry = it.previous();
            if (entry != null)
            {
                entry.redo();
            }
        }

        if (removeHistory.contains(playerName.toLowerCase()))
        {
            removeHistory.remove(playerName.toLowerCase());
        }

        return count;
    }

    public List<RollbackEntry> getEntriesAtLocation(final Location location)
    {
        final int testX = location.getBlockX();
        final short testY = (short) location.getBlockY();
        final int testZ = location.getBlockZ();
        final String testWorldName = location.getWorld().getName();

        List<RollbackEntry> entries = new ArrayList<>();
        for (String playername : history.keySet())
        {
            for (RollbackEntry entry : history.get(playername.toLowerCase()))
            {
                if (testX == entry.x && testY == entry.y && testZ == entry.z && testWorldName.equals(entry.worldName))
                {
                    entries.add(0, entry);
                }
            }
        }

        return entries;
    }

    @EventHandler(ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        final Player player = event.getPlayer();

        if (!event.hasItem()
                || event.getItem().getType() != Material.STICK
                || !plugin.al.isAdmin(player))
        {
            return;
        }

        event.setCancelled(true);

        final Location location = DepreciationAggregator.getTargetBlock(player, null, 5).getLocation();
        final List<RollbackEntry> entries = plugin.rb.getEntriesAtLocation(location);

        if (entries.isEmpty())
        {
            FUtil.playerMsg(player, "No block edits at that location.");
            return;
        }

        FUtil.playerMsg(player, "Block edits at ("
                + ChatColor.WHITE + "x" + location.getBlockX()
                + ", y" + location.getBlockY()
                + ", z" + location.getBlockZ()
                + ChatColor.BLUE + ")" + ChatColor.WHITE + ":", ChatColor.BLUE);
        for (RollbackEntry entry : entries)
        {
            FUtil.playerMsg(player, " - " + ChatColor.BLUE + entry.author + " " + entry.getType() + " "
                    + StringUtils.capitalize(entry.getMaterial().toString().toLowerCase()) + (entry.data == 0 ? "" : ":" + entry.data));
        }
    }

    private List<RollbackEntry> getEntriesByPlayer(String playerName)
    {
        playerName = playerName.toLowerCase();
        List<RollbackEntry> playerEntryList = history.get(playerName.toLowerCase());
        if (playerEntryList == null)
        {
            playerEntryList = new ArrayList<>();
            history.put(playerName.toLowerCase(), playerEntryList);
        }
        return playerEntryList;
    }

}
