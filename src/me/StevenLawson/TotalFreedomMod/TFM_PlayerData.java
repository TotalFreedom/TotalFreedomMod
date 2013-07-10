package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TFM_PlayerData
{
    public final static Map<Player, TFM_PlayerData> userinfo = new HashMap<Player, TFM_PlayerData>();
    private final Player player;
    private final String ip_address;
    private final String player_name;
    private boolean user_frozen = false;
    private boolean is_muted = false;
    private boolean is_halted = false;
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
    private BukkitTask mp44_schedule_id = null;
    private boolean mp44_armed = false;
    private boolean mp44_firing = false;
    private BukkitTask lockup_schedule_id = null;
    private String last_message = "";
    private boolean in_adminchat = false;
    private boolean all_commands_blocked = false;
    private Boolean superadmin_id_verified = null;
    private String last_command = "";
    private boolean cmdspy_enabled = false;

    public TFM_PlayerData(Player player)
    {
        this.player = player;
        this.ip_address = player.getAddress().getAddress().getHostAddress();
        this.player_name = player.getName();
    }

    public static TFM_PlayerData getPlayerData(Player p)
    {
        TFM_PlayerData playerdata = TFM_PlayerData.userinfo.get(p);

        if (playerdata == null)
        {
            Iterator<Entry<Player, TFM_PlayerData>> it = userinfo.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<Player, TFM_PlayerData> pair = it.next();
                TFM_PlayerData playerdata_test = pair.getValue();

                if (playerdata_test.player_name.equalsIgnoreCase(p.getName()))
                {
                    if (Bukkit.getOnlineMode())
                    {
                        playerdata = playerdata_test;
                        break;
                    }
                    else
                    {
                        if (playerdata_test.ip_address.equalsIgnoreCase(p.getAddress().getAddress().getHostAddress()))
                        {
                            playerdata = playerdata_test;
                            break;
                        }
                    }
                }
            }
        }

        if (playerdata == null)
        {
            playerdata = new TFM_PlayerData(p);
            TFM_PlayerData.userinfo.put(p, playerdata);
        }

        return playerdata;
    }

    public String getIpAddress()
    {
        return ip_address;
    }

    public String getPlayerName()
    {
        return player_name;
    }

    public boolean isOrbiting()
    {
        return is_orbiting;
    }

    public void startOrbiting(double orbit_strength)
    {
        this.is_orbiting = true;
        this.orbit_strength = orbit_strength;
    }

    public void stopOrbiting()
    {
        is_orbiting = false;
    }

    public double orbitStrength()
    {
        return orbit_strength;
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
        return user_caged;
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
        return user_cage_pos;
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
        return msg_count;
    }

    public void incrementBlockDestroyCount()
    {
        this.block_destroy_total++;
    }

    public int getBlockDestroyCount()
    {
        return block_destroy_total;
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
        return block_place_total;
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
        return freecam_destroy_count;
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
        return freecam_place_count;
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
        return mob_thrower_creature;
    }

    public double mobThrowerSpeed()
    {
        return mob_thrower_speed;
    }

    public boolean mobThrowerEnabled()
    {
        return mob_thrower_enabled;
    }

    public void enqueueMob(LivingEntity mob)
    {
        mob_thrower_queue.add(mob);
        if (mob_thrower_queue.size() > 4)
        {
            LivingEntity oldmob = mob_thrower_queue.remove(0);
            if (oldmob != null)
            {
                oldmob.damage(500.0);
            }
        }
    }

    public void startArrowShooter(TotalFreedomMod plugin)
    {
        this.stopArrowShooter();
        this.mp44_schedule_id = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new ArrowShooter(this.player), 1L, 1L);
        mp44_firing = true;
    }

    public void stopArrowShooter()
    {
        if (this.mp44_schedule_id != null)
        {
            this.mp44_schedule_id.cancel();
            this.mp44_schedule_id = null;
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
            Arrow shot_arrow = _player.launchProjectile(Arrow.class);
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
        return mp44_armed;
    }

    public boolean toggleMP44Firing()
    {
        this.mp44_firing = !this.mp44_firing;
        return mp44_firing;
    }

    public boolean isMuted()
    {
        return is_muted;
    }

    public void setMuted(boolean is_muted)
    {
        this.is_muted = is_muted;
    }

    public boolean isHalted()
    {
        return is_halted;
    }

    public void setHalted(boolean is_halted)
    {
        this.is_halted = is_halted;

        if (is_halted)
        {
            player.setOp(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.setFlying(false);
            player.setDisplayName(player_name);
            player.closeInventory();
            player.setTotalExperience(0);

            stopOrbiting();
            setFrozen(true);
            setMuted(true);

            player.sendMessage(ChatColor.GRAY + "You have been halted, don't move!");
        }
        else
        {
            player.setOp(true);
            player.setGameMode(GameMode.CREATIVE);
            setFrozen(false);
            setMuted(false);

            player.sendMessage(ChatColor.GRAY + "You are no longer halted.");
        }
        
    }

    public BukkitTask getLockupScheduleID()
    {
        return lockup_schedule_id;
    }

    public void setLockupScheduleID(BukkitTask lockup_schedule_id)
    {
        this.lockup_schedule_id = lockup_schedule_id;
    }

    public void setLastMessage(String last_message)
    {
        this.last_message = last_message;
    }

    public String getLastMessage()
    {
        return last_message;
    }

    public void setAdminChat(boolean in_adminchat)
    {
        this.in_adminchat = in_adminchat;
    }

    public boolean inAdminChat()
    {
        return in_adminchat;
    }

    public boolean allCommandsBlocked()
    {
        return all_commands_blocked;
    }

    public void setCommandsBlocked(boolean commands_blocked)
    {
        this.all_commands_blocked = commands_blocked;
    }

    //If someone logs in to telnet or minecraft, and they are an admin, make sure that they are using a username that is associated with their IP.
    //After the check for this is done in TFM_PlayerListener, never change it elsewhere.
    public Boolean isSuperadminIdVerified()
    {
        return superadmin_id_verified;
    }

    //If someone logs in to telnet or minecraft, and they are an admin, make sure that they are using a username that is associated with their IP.
    //After the check for this is done in TFM_PlayerListener, never change it elsewhere.
    public void setSuperadminIdVerified(Boolean superadmin_id_verified)
    {
        this.superadmin_id_verified = superadmin_id_verified;
    }

    public String getLastCommand()
    {
        return last_command;
    }

    public void setLastCommand(String last_command)
    {
        this.last_command = last_command;
    }

    public void setCommandSpy(boolean cmdspy_enabled)
    {
        this.cmdspy_enabled = cmdspy_enabled;
    }

    public boolean cmdspyEnabled()
    {
        return cmdspy_enabled;
    }
}
