package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;


public class TFM_RollbackEntry
{

    private Location location;
    private Material material;
    private byte data;

    public TFM_RollbackEntry()
    {
    }

    public TFM_RollbackEntry(Block block)
    {
        location = block.getLocation();
        material = block.getType();
        data = block.getData();
    }

    public void setBlock(Block block)
    {
        location = block.getLocation();
        material = block.getType();
        data = block.getData();
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setMaterial(Material material)
    {
        this.material = material;
    }

    public void setData(byte data)
    {
        this.data = data;
    }

    public void restore()
    {
        Block b = location.getWorld().getBlockAt(location);
        b.setType(material);
        b.setData(data);
    }
}
