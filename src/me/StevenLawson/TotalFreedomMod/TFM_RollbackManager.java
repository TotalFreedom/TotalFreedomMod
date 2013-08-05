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
    private static final Map<String, List<TFM_RollbackManager_Entry>> PLAYER_HISTORY_MAP = new HashMap<String, List<TFM_RollbackManager_Entry>>();

    private TFM_RollbackManager()
    {
        throw new AssertionError();
    }

    public static void blockPlace(org.bukkit.event.block.BlockPlaceEvent event)
    {
        storeEntry(event.getPlayer(), new TFM_RollbackManager_Entry(event.getBlock(), TFM_RollbackManager_EntryType.BLOCK_PLACE));
    }

    public static void blockBreak(org.bukkit.event.block.BlockBreakEvent event)
    {
        storeEntry(event.getPlayer(), new TFM_RollbackManager_Entry(event.getBlock(), TFM_RollbackManager_EntryType.BLOCK_BREAK));
    }

    private static void storeEntry(Player player, TFM_RollbackManager_Entry entry)
    {
        List<TFM_RollbackManager_Entry> playerEntryList = getPlayerEntryList(player.getName());
        if (playerEntryList != null)
        {
            playerEntryList.add(0, entry);
        }
    }

    public static int purgeEntries()
    {
        int count = 0;
        Iterator<List<TFM_RollbackManager_Entry>> it = PLAYER_HISTORY_MAP.values().iterator();
        while (it.hasNext())
        {
            List<TFM_RollbackManager_Entry> playerEntryList = it.next();
            if (playerEntryList != null)
            {
                count += playerEntryList.size();
                playerEntryList.clear();
            }
        }
        return count;
    }

    public static int purgeEntries(String playerName)
    {
        List<TFM_RollbackManager_Entry> playerEntryList = getPlayerEntryList(playerName);
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
        List<TFM_RollbackManager_Entry> playerEntryList = getPlayerEntryList(playerName);
        if (playerEntryList != null)
        {
            int count = playerEntryList.size();
            Iterator<TFM_RollbackManager_Entry> it = playerEntryList.iterator();
            while (it.hasNext())
            {
                TFM_RollbackManager_Entry entry = it.next();
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

    private static List<TFM_RollbackManager_Entry> getPlayerEntryList(String playerName)
    {
        playerName = playerName.toLowerCase();
        List<TFM_RollbackManager_Entry> playerEntryList = PLAYER_HISTORY_MAP.get(playerName);
        if (playerEntryList == null)
        {
            playerEntryList = new ArrayList<TFM_RollbackManager_Entry>();
            PLAYER_HISTORY_MAP.put(playerName, playerEntryList);
        }
        return playerEntryList;
    }

    private enum TFM_RollbackManager_EntryType
    {
        BLOCK_PLACE, BLOCK_BREAK
    }

    private static class TFM_RollbackManager_Entry
    {
        private final Location location;
        private final Material material;
        private final byte data;

        public TFM_RollbackManager_Entry(Block block, TFM_RollbackManager_EntryType entryType)
        {
            this.location = block.getLocation();
            if (entryType == TFM_RollbackManager_EntryType.BLOCK_BREAK)
            {
                this.material = block.getType();
                this.data = block.getData();
            }
            else
            {
                this.material = Material.AIR;
                this.data = 0;
            }
        }

        public void restore()
        {
            Block b = this.location.getWorld().getBlockAt(this.location);
            b.setType(this.material);
            b.setData(this.data);
        }
    }
}
