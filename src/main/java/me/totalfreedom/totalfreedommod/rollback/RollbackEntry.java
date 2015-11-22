package me.totalfreedom.totalfreedommod.rollback;

import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class RollbackEntry
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

    public RollbackEntry(String author, Block block, EntryType entryType)
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
            this.data = DepreciationAggregator.getData_Block(block);
            this.isBreak = true;
        }
        else
        {
            this.blockMaterial = block.getType();
            this.data = DepreciationAggregator.getData_Block(block);
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
            FLog.warning("Could not get location of rollback entry at (" + worldName + ":" + x + "," + y + "," + x + ")!");
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
            DepreciationAggregator.setData_Block(block, data);
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
            DepreciationAggregator.setData_Block(block, data);
        }
    }
}
