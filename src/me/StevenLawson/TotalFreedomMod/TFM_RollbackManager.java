package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
        BLOCK_PLACE, BLOCK_BREAK;
        
        @Override
        public String toString()
        {
            if (this == BLOCK_PLACE)
            {
                return "placed";
            }
            else
            {
                return "broke";
            }
        }
    }

    public static class RollbackEntry
    {
        private final String author;
        private final Location location;
        private final int toBlockId; // ints have less overhead than Materials
        private final int fromBlockId;
        private final byte data;

        private RollbackEntry(String author, Block block, EntryType entryType)
        {
            this.location = block.getLocation().clone();
            this.author = author;

            if (entryType == EntryType.BLOCK_BREAK)
            {
                fromBlockId = block.getTypeId();
                toBlockId = Material.AIR.getId();
                data = block.getData();
            }
            else
            {
                fromBlockId = Material.AIR.getId();
                toBlockId = block.getTypeId();
                data = 0;
            }
        }

        public String getAuthor()
        {
            return author;
        }

        public Location getLocation()
        {
            return location;
        }

        public Material getFromMaterial()
        {
            return Material.getMaterial(fromBlockId);
        }
        
        public Material getToMaterial()
        {
            return Material.getMaterial(toBlockId);
        }

        public byte getData()
        {
            return data;
        }

        public EntryType getType()
        {
            return (getFromMaterial() == Material.AIR ? EntryType.BLOCK_PLACE : EntryType.BLOCK_BREAK);
        }

        public void restore()
        {
            Block block = location.getWorld().getBlockAt(location);
            block.setType(getFromMaterial());
            block.setData(data);
        }
    }
}
