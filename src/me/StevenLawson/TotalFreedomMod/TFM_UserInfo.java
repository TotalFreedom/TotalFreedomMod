package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
    private Material cage_material_outer = Material.GLASS;
    private Material cage_material_inner = Material.AIR;
    private boolean is_orbiting = false;
    private double orbit_strength = 10.0;
    private boolean mob_thrower_enabled = false;
    private CreatureType mob_thrower_creature = CreatureType.PIG;
    private double mob_thrower_speed = 4.0;
    private List<LivingEntity> mobqueue = new ArrayList<LivingEntity>();
    private int schedule_id = -1;

    public TFM_UserInfo()
    {
    }
    
    public static TFM_UserInfo getPlayerData(Player p, TotalFreedomMod tfm)
    {
        TFM_UserInfo playerdata = tfm.userinfo.get(p);
        if (playerdata == null)
        {
            playerdata = new TFM_UserInfo();
            tfm.userinfo.put(p, playerdata);
        }
        return playerdata;
    }
    
    public boolean isOrbiting()
    {
        return this.is_orbiting;
    }

    public void startOrbiting(double orbit_strength)
    {
        this.is_orbiting = true;
        this.orbit_strength = orbit_strength;
    }
    
    public void stopOrbiting()
    {
        this.is_orbiting = false;
    }

    public double orbitStrength()
    {
        return this.orbit_strength;
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

    public void enableMobThrower(CreatureType mob_thrower_creature, double mob_thrower_speed)
    {
        this.mob_thrower_enabled = true;
        this.mob_thrower_creature = mob_thrower_creature;
        this.mob_thrower_speed = mob_thrower_speed;
    }
    
    public void disableMobThrower()
    {
        this.mob_thrower_enabled = false;
    }

    public CreatureType mobThrowerCreature()
    {
        return this.mob_thrower_creature;
    }

    public double mobThrowerSpeed()
    {
        return this.mob_thrower_speed;
    }
    
    public boolean mobThrowerEnabled()
    {
        return this.mob_thrower_enabled;
    }
    
    public void enqueueMob(LivingEntity mob)
    {
        mobqueue.add(mob);
        if (mobqueue.size() > 4)
        {
            LivingEntity oldmob = mobqueue.remove(0);
            if (oldmob != null)
            {
                oldmob.damage(20);
            }
        }
    }
    
    void startArrowShooter(int schedule_id)
    {
        this.schedule_id = schedule_id;
    }
    
    void stopArrowShooter()
    {
        if (this.schedule_id != -1)
        {
            Bukkit.getScheduler().cancelTask(this.schedule_id);
            this.schedule_id = -1;
        }
    }
}
