package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TFM_RollbackManager
{
    private static final Map<String, List<RollbackEntry>> PLAYER_HISTORY_MAP = new HashMap<String, List<RollbackEntry>>();

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

    public static int purgeEntries()
    {
        Iterator<List<RollbackEntry>> it = PLAYER_HISTORY_MAP.values().iterator();
        while (it.hasNext())
        {
            List<RollbackEntry> playerEntryList = it.next();
            if (playerEntryList != null)
            {
                playerEntryList.clear();
            }
        }
        return PLAYER_HISTORY_MAP.size();
    }

    public static int purgeEntries(String playerName)
    {
        List<RollbackEntry> playerEntryList = getEntriesByPlayer(playerName);
        if (playerEntryList != null)
        {
            int count = playerEntryList.size();
            playerEntryList.clear();
            return count;
        }
        return 0;
    }

    public static boolean canRollback(String playerName)
    {
        return PLAYER_HISTORY_MAP.containsKey(playerName.toLowerCase());
    }

    public static int rollback(String playerName)
    {
        List<RollbackEntry> playerEntryList = getEntriesByPlayer(playerName);
        if (playerEntryList != null)
        {
            int count = playerEntryList.size();
            Iterator<RollbackEntry> it = playerEntryList.iterator();
            while (it.hasNext())
            {
                RollbackEntry entry = it.next();
                if (entry != null)
                {
                    entry.restore();
                }
                it.remove();
            }
            return count;
        }
        return 0;
    }

    public static List<RollbackEntry> getEntriesAtLocation(Location location)
    {
        location = location.clone();

        List<RollbackEntry> entries = new ArrayList<RollbackEntry>();
        for (String playername : PLAYER_HISTORY_MAP.keySet())
        {
            for (RollbackEntry entry : PLAYER_HISTORY_MAP.get(playername))
            {
                if (entry.getLocation().equals(location))
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
        List<RollbackEntry> playerEntryList = PLAYER_HISTORY_MAP.get(playerName);
        if (playerEntryList == null)
        {
            playerEntryList = new ArrayList<RollbackEntry>();
            PLAYER_HISTORY_MAP.put(playerName, playerEntryList);
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
        private final String author;
        private final String worldName;
        private final int x;
        private final short y;
        private final int z;
        private final short blockId;
        private final byte data;
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
                this.blockId = (short) block.getTypeId();
                this.data = block.getData();
                this.isBreak = true;
            }
            else
            {
                blockId = (short) block.getTypeId();
                data = 0;
                this.isBreak = false;
            }
        }

        public String getAuthor()
        {
            return author;
        }

        public Location getLocation()
        {
            try
            {
                return new Location(Bukkit.getWorld(worldName), (double) x, (double) y, (double) z);
            }
            catch (Exception ex)
            {
                TFM_Log.warning("Could not get location of rollback entry at (" + worldName + ":" + x + "," + y + "," + x + ")!");
            }
            return null;
        }

        public Material getMaterial()
        {
            return Material.getMaterial(blockId);
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public int getZ()
        {
            return z;
        }

        public byte getData()
        {
            return data;
        }

        public EntryType getType()
        {
            return (isBreak ? EntryType.BLOCK_BREAK : EntryType.BLOCK_PLACE);
        }

        public void restore()
        {
            Block block = Bukkit.getWorld(worldName).getBlockAt(x, y, z);
            if (isBreak)
            {
                block.setType(getMaterial());
                block.setData(data);
            }
            else
            {
                block.setType(Material.AIR);
            }
        }
    }
}
