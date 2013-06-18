package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

public class TFM_RollbackManager
{
    public static Map<String, List<TFM_RollbackEntry>> entries = new HashMap<String, List<TFM_RollbackEntry>>();

    public static void blockUpdate(OfflinePlayer player, Block block)
    {
        List <TFM_RollbackEntry> e;
        if (entries.containsKey(player.getName()))
        {
            e = entries.get(player.getName());
        }
        else
        {
            e = new ArrayList<TFM_RollbackEntry>();
        }
        e.add(0, new TFM_RollbackEntry(block));
        entries.put(player.getName(), e);
    }

    public static void blockUpdate(OfflinePlayer player, TFM_RollbackEntry entry)
    {
        List <TFM_RollbackEntry> e;
        if (entries.containsKey(player.getName()))
        {
            e = entries.get(player.getName());
        }
        else
        {
            e = new ArrayList<TFM_RollbackEntry>();
        }
        e.add(0, entry);
        entries.put(player.getName(), e);
    }

    public static int rollback(OfflinePlayer player)
    {
        if (!canRollback(player.getName()))
        {
            TFM_Log.severe("Could not rollback player: " + player.getName() + "! No entries are set");
            return 0;
        }

        List<TFM_RollbackEntry> e = entries.get(player.getName());
        int counter = 0;
        for (TFM_RollbackEntry entry : e)
        {
            entry.restore();
            counter++;
        }
        entries.remove(player.getName());
        return counter;
    }

    public static boolean canRollback(String player)
    {
        return entries.containsKey(player);
    }

    public static int purgeEntries()
    {
        int counter = entries.size();
        entries.clear();
        return counter;
    }

    public static int purgeEntries(String player)
    {
        if (!canRollback(player))
        {
            return 0;
        }

        int counter = entries.get(player).size();
        entries.remove(player);
        return counter;
    }
 }
