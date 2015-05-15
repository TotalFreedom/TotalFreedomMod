package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_RollbackManager
{
    private static final Map<String, List<RollbackEntry>> PLAYER_HISTORY = new HashMap<String, List<RollbackEntry>>();
    private static final List<String> REMOVE_ROLLBACK_HISTORY = new ArrayList<String>();

    private TFM_RollbackManager()
    {
        throw new AssertionError();
    }

    public static void blockPlace(org.bukkit.event.block.BlockPlaceEvent event)
    {
        storeEntry(event.getPlayer(), new RollbackEntry(event.getPlayer().getName(), event.getBlock(), EntryType.BLOCK_PLACE));
    }

    public static void blockBreak(org.bukkit.event.block.BlockBreakEvent event)
    {
        storeEntry(event.getPlayer(), new RollbackEntry(event.getPlayer().getName(), event.getBlock(), EntryType.BLOCK_BREAK));
    }

    private static void storeEntry(Player player, RollbackEntry entry)
    {
        List<RollbackEntry> playerEntryList = getEntriesByPlayer(player.getName());

        if (playerEntryList != null)
        {
            playerEntryList.add(0, entry);
        }
    }

    // May return null
    public static String findPlayer(String partial)
    {
        partial = partial.toLowerCase();

        for (String player : PLAYER_HISTORY.keySet())
        {
            if (player.toLowerCase().equals(partial))
            {
                return player;
            }
        }

        for (String player : PLAYER_HISTORY.keySet())
        {
            if (player.toLowerCase().contains(partial))
            {
                return player;
            }
        }

        return null;
    }

    public static int purgeEntries()
    {
        Iterator<List<RollbackEntry>> it = PLAYER_HISTORY.values().iterator();
        while (it.hasNext())
        {
            List<RollbackEntry> playerEntryList = it.next();
            if (playerEntryList != null)
            {
                playerEntryList.clear();
            }
        }
        return PLAYER_HISTORY.size();
    }

    public static int purgeEntries(String playerName)
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

    public static boolean canRollback(String playerName)
    {
        return PLAYER_HISTORY.containsKey(playerName.toLowerCase()) && !PLAYER_HISTORY.get(playerName.toLowerCase()).isEmpty();
    }

    public static boolean canUndoRollback(String playerName)
    {
        return REMOVE_ROLLBACK_HISTORY.contains(playerName.toLowerCase());
    }

    public static int rollback(final String playerName)
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

        if (!REMOVE_ROLLBACK_HISTORY.contains(playerName.toLowerCase()))
        {
            REMOVE_ROLLBACK_HISTORY.add(playerName.toLowerCase());
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (REMOVE_ROLLBACK_HISTORY.contains(playerName.toLowerCase()))
                {
                    REMOVE_ROLLBACK_HISTORY.remove(playerName.toLowerCase());
                    purgeEntries(playerName);
                }
            }
        }.runTaskLater(TotalFreedomMod.plugin, 40L * 20L);
        return count;
    }

    public static int undoRollback(String playerName)
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

        if (REMOVE_ROLLBACK_HISTORY.contains(playerName.toLowerCase()))
        {
            REMOVE_ROLLBACK_HISTORY.remove(playerName.toLowerCase());
        }

        return count;
    }

    public static List<RollbackEntry> getEntriesAtLocation(final Location location)
    {
        final int testX = location.getBlockX();
        final short testY = (short) location.getBlockY();
        final int testZ = location.getBlockZ();
        final String testWorldName = location.getWorld().getName();

        List<RollbackEntry> entries = new ArrayList<RollbackEntry>();
        for (String playername : PLAYER_HISTORY.keySet())
        {
            for (RollbackEntry entry : PLAYER_HISTORY.get(playername.toLowerCase()))
            {
                if (testX == entry.x && testY == entry.y && testZ == entry.z && testWorldName.equals(entry.worldName))
                {
                    entries.add(0, entry);
                }
            }
        }

        return entries;
    }

    private static List<RollbackEntry> getEntriesByPlayer(String playerName)
    {
        playerName = playerName.toLowerCase();
        List<RollbackEntry> playerEntryList = PLAYER_HISTORY.get(playerName.toLowerCase());
        if (playerEntryList == null)
        {
            playerEntryList = new ArrayList<RollbackEntry>();
            PLAYER_HISTORY.put(playerName.toLowerCase(), playerEntryList);
        }
        return playerEntryList;
    }

    public enum EntryType
    {
        BLOCK_PLACE("placed"),
        BLOCK_BREAK("broke");
        private final String action;

        private EntryType(String action)
        {
            this.action = action;
        }

        @Override
        public String toString()
        {
            return this.action;
        }
    }

    public static class RollbackEntry
    {
        // Use of primitives to decrease overhead
        public final String author;
        public final String worldName;
        public final int x;
        public final short y;
        public final int z;
        public final byte data;
        public final Material blockMaterial;
        private final boolean isBreak;

        private RollbackEntry(String author, Block block, EntryType entryType)
        {
            final Location location = block.getLocation();

            this.x = location.getBlockX();
            this.y = (short) location.getBlockY();
            this.z = location.getBlockZ();
            this.worldName = location.getWorld().getName();
            this.author = author;

            if (entryType == EntryType.BLOCK_BREAK)
            {
                this.blockMaterial = block.getType();
                this.data = TFM_DepreciationAggregator.getData_Block(block);
                this.isBreak = true;
            }
            else
            {
                this.blockMaterial = block.getType();
                this.data = TFM_DepreciationAggregator.getData_Block(block);
                this.isBreak = false;
            }
        }

        public Location getLocation()
        {
            try
            {
                return new Location(Bukkit.getWorld(worldName), x, (int) y, z);
            }
            catch (Exception ex)
            {
                TFM_Log.warning("Could not get location of rollback entry at (" + worldName + ":" + x + "," + y + "," + x + ")!");
            }
            return null;
        }

        public Material getMaterial()
        {
            return blockMaterial;
        }

        public EntryType getType()
        {
            return (isBreak ? EntryType.BLOCK_BREAK : EntryType.BLOCK_PLACE);
        }

        public void restore()
        {
            final Block block = Bukkit.getWorld(worldName).getBlockAt(x, y, z);
            if (isBreak)
            {
                block.setType(getMaterial());
                TFM_DepreciationAggregator.setData_Block(block, data);
            }
            else
            {
                block.setType(Material.AIR);
            }
        }

        public void redo()
        {
            final Block block = Bukkit.getWorld(worldName).getBlockAt(x, y, z);

            if (isBreak)
            {
                block.setType(Material.AIR);
            }
            else
            {
                block.setType(getMaterial());
                TFM_DepreciationAggregator.setData_Block(block, data);
            }
        }
    }
}
