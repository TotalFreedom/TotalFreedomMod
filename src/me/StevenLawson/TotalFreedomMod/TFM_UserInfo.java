package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;

public class TFM_UserInfo
{
    private boolean user_frozen = false;
    private int msg_count = 0;
    private int block_destroy_total = 0;
    private int freecam_destroy_count = 0;
    private int freecam_place_count = 0;
    private boolean forced_death = false;
    private boolean user_caged = false;
    private Location user_cage_pos;
    private List<TFM_BlockData> user_cage_history = new ArrayList<TFM_BlockData>();
    private Material cage_material_outer;
    private Material cage_material_inner;

    public TFM_UserInfo()
    {
    }

    public void setCaged(boolean state)
    {
        this.user_caged = state;
    }

    public void setCaged(boolean state, Location location, Material material_outer, Material material_inner)
    {
        this.user_caged = state;
        this.user_cage_pos = location;
        this.cage_material_outer = material_outer;
        this.cage_material_inner = material_inner;
    }

    public boolean isCaged()
    {
        return this.user_caged;
    }

    public enum CageLayer
    {
        INNER, OUTER
    }

    public Material getCageMaterial(CageLayer layer)
    {
        switch (layer)
        {
            case OUTER:
                return this.cage_material_outer;
            case INNER:
                return this.cage_material_inner;
            default:
                return this.cage_material_outer;
        }
    }

    public Location getCagePos()
    {
        return this.user_cage_pos;
    }

    public void clearHistory()
    {
        this.user_cage_history.clear();
    }

    public void insertHistoryBlock(Location location, Material material)
    {
        this.user_cage_history.add(new TFM_BlockData(location, material));
    }

    public void regenerateHistory()
    {
        for (TFM_BlockData blockdata : this.user_cage_history)
        {
            blockdata.location.getBlock().setType(blockdata.material);
        }
    }

    class TFM_BlockData
    {
        public Material material;
        public Location location;

        public TFM_BlockData(Location location, Material material)
        {
            this.location = location;
            this.material = material;
        }
    }

    public boolean getForcedDeath()
    {
        return this.forced_death;
    }

    void setForcedDeath(boolean forced_death)
    {
        this.forced_death = forced_death;
    }

    public boolean isFrozen()
    {
        return this.user_frozen;
    }

    public void setFrozen(boolean fr)
    {
        this.user_frozen = fr;
    }

    public void resetMsgCount()
    {
        this.msg_count = 0;
    }

    public void incrementMsgCount()
    {
        this.msg_count++;
    }

    public int getMsgCount()
    {
        return this.msg_count;
    }

    public void incrementBlockDestroyCount()
    {
        this.block_destroy_total++;
    }

    public int getBlockDestroyCount()
    {
        return this.block_destroy_total;
    }

    public void resetBlockDestroyCount()
    {
        this.block_destroy_total = 0;
    }

    public void incrementFreecamDestroyCount()
    {
        this.freecam_destroy_count++;
    }

    public int getFreecamDestroyCount()
    {
        return this.freecam_destroy_count;
    }

    public void resetFreecamDestroyCount()
    {
        this.freecam_destroy_count = 0;
    }

    public void incrementFreecamPlaceCount()
    {
        this.freecam_place_count++;
    }

    public int getFreecamPlaceCount()
    {
        return this.freecam_place_count;
    }

    public void resetFreecamPlaceCount()
    {
        this.freecam_place_count = 0;
    }
}
