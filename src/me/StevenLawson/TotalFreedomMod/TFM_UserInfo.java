package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;

public class TFM_UserInfo
{
    private Player player;
    private boolean user_frozen = false;
    private int msg_count = 0;
    private int block_destroy_total = 0;
    private int block_place_total = 0;
    private int freecam_destroy_count = 0;
    private int freecam_place_count = 0;
    private boolean user_caged = false;
    private Location user_cage_pos;
    private List<TFM_BlockData> user_cage_history = new ArrayList<TFM_BlockData>();
    private Material cage_material_outer = Material.GLASS;
    private Material cage_material_inner = Material.AIR;
    private boolean is_orbiting = false;
    private double orbit_strength = 10.0;
    private boolean mob_thrower_enabled = false;
    private EntityType mob_thrower_creature = EntityType.PIG;
    private double mob_thrower_speed = 4.0;
    private List<LivingEntity> mob_thrower_queue = new ArrayList<LivingEntity>();
    private int mp44_schedule_id = -1;
    private boolean mp44_armed = false;
    private boolean mp44_firing = false;
    
    public static Map<Player, TFM_UserInfo> userinfo = new HashMap<Player, TFM_UserInfo>();

    private TFM_UserInfo(Player player)
    {
        this.player = player;
    }
    
    public static TFM_UserInfo getPlayerData(Player p)
    {
        TFM_UserInfo playerdata = TFM_UserInfo.userinfo.get(p);
        if (playerdata == null)
        {
            playerdata = new TFM_UserInfo(p);
            TFM_UserInfo.userinfo.put(p, playerdata);
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
    
    public void incrementBlockPlaceCount()
    {
        this.block_place_total++;
    }

    public int getBlockPlaceCount()
    {
        return this.block_place_total;
    }

    public void resetBlockPlaceCount()
    {
        this.block_place_total = 0;
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

    public void enableMobThrower(EntityType mob_thrower_creature, double mob_thrower_speed)
    {
        this.mob_thrower_enabled = true;
        this.mob_thrower_creature = mob_thrower_creature;
        this.mob_thrower_speed = mob_thrower_speed;
    }
    
    public void disableMobThrower()
    {
        this.mob_thrower_enabled = false;
    }

    public EntityType mobThrowerCreature()
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
        mob_thrower_queue.add(mob);
        if (mob_thrower_queue.size() > 4)
        {
            LivingEntity oldmob = mob_thrower_queue.remove(0);
            if (oldmob != null)
            {
                oldmob.damage(20);
            }
        }
    }
    
    public void startArrowShooter(TotalFreedomMod plugin)
    {
        this.stopArrowShooter();
        this.mp44_schedule_id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new ArrowShooter(this.player), 1L, 1L);
        mp44_firing = true;
    }
    
    public void stopArrowShooter()
    {
        if (this.mp44_schedule_id != -1)
        {
            Bukkit.getScheduler().cancelTask(this.mp44_schedule_id);
            this.mp44_schedule_id = -1;
        }
        mp44_firing = false;
    }
    
    class ArrowShooter implements Runnable
    {
        private Player _player;
        
        public ArrowShooter(Player player)
        {
            this._player = player;
        }

        @Override
        public void run()
        {
            Arrow shot_arrow = _player.shootArrow();
            shot_arrow.setVelocity(shot_arrow.getVelocity().multiply(2.0));
        }
    }
    
    public void armMP44()
    {
        mp44_armed = true;
        this.stopArrowShooter();
    }
    
    public void disarmMP44()
    {
        mp44_armed = false;
        this.stopArrowShooter();
    }
    
    public boolean isMP44Armed()
    {
        return this.mp44_armed;
    }
    
    public boolean toggleMP44Firing()
    {
        this.mp44_firing = !this.mp44_firing;
        return this.mp44_firing;
    }
}
