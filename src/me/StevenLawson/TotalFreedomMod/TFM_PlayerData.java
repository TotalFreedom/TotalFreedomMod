package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import me.StevenLawson.TotalFreedomMod.Bridge.TFM_EssentialsBridge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TFM_PlayerData
{
    public final static Map<Player, TFM_PlayerData> userinfo = new HashMap<Player, TFM_PlayerData>();
    private final Player player;
    private final String ip;
    private final UUID uuid;
    private boolean isFrozen = false;
    private boolean isMuted = false;
    private boolean isHalted = false;
    private int messageCount = 0;
    private int totalBlockDestroy = 0;
    private int totalBlockPlace = 0;
    private int freecamDestroyCount = 0;
    private int freecamPlaceCount = 0;
    private boolean isCaged = false;
    private Location cagePosition;
    private List<TFM_BlockData> cageHistory = new ArrayList<TFM_BlockData>();
    private Material cageOuterMaterial = Material.GLASS;
    private Material cageInnerMatterial = Material.AIR;
    private boolean isOrbiting = false;
    private double orbitStrength = 10.0;
    private boolean mobThrowerEnabled = false;
    private EntityType mobThrowerEntity = EntityType.PIG;
    private double mobThrowerSpeed = 4.0;
    private List<LivingEntity> mobThrowerQueue = new ArrayList<LivingEntity>();
    private BukkitTask mp44ScheduleId = null;
    private boolean mp44Armed = false;
    private boolean mp44Firing = false;
    private BukkitTask lockupScheduleId = null;
    private String lastMessage = "";
    private boolean inAdminchat = false;
    private boolean allCommandsBlocked = false;
    private boolean verifiedSuperadminId = false;
    private String lastCommand = "";
    private boolean cmdspyEnabled = false;
    private String tag = null;

    private TFM_PlayerData(Player player)
    {
        this.player = player;
        this.uuid = TFM_UuidManager.getUniqueId(player.getName());
        this.ip = player.getAddress().getAddress().getHostAddress();
    }

    public static TFM_PlayerData getPlayerData(Player player)
    {
        TFM_PlayerData playerdata = TFM_PlayerData.userinfo.get(player);

        if (playerdata != null)
        {
            return playerdata;
        }

        Iterator<Entry<Player, TFM_PlayerData>> it = userinfo.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<Player, TFM_PlayerData> pair = it.next();
            TFM_PlayerData playerdataTest = pair.getValue();

            if (playerdataTest.player.getName().equalsIgnoreCase(player.getName()))
            {
                if (Bukkit.getOnlineMode())
                {
                    playerdata = playerdataTest;
                    break;
                }
                else
                {
                    if (playerdataTest.ip.equalsIgnoreCase(player.getAddress().getAddress().getHostAddress()))
                    {
                        playerdata = playerdataTest;
                        break;
                    }
                }
            }
        }

        if (playerdata != null)
        {
            return playerdata;
        }

        playerdata = new TFM_PlayerData(player);
        TFM_PlayerData.userinfo.put(player, playerdata);

        return playerdata;
    }

    public String getIpAddress()
    {
        return this.ip;
    }

    public UUID getUniqueId()
    {
        return uuid;
    }

    public boolean isOrbiting()
    {
        return this.isOrbiting;
    }

    public void startOrbiting(double strength)
    {
        this.isOrbiting = true;
        this.orbitStrength = strength;
    }

    public void stopOrbiting()
    {
        this.isOrbiting = false;
    }

    public double orbitStrength()
    {
        return this.orbitStrength;
    }

    public void setCaged(boolean state)
    {
        this.isCaged = state;
    }

    public void setCaged(boolean state, Location location, Material outer, Material inner)
    {
        this.isCaged = state;
        this.cagePosition = location;
        this.cageOuterMaterial = outer;
        this.cageInnerMatterial = inner;
    }

    public boolean isCaged()
    {
        return this.isCaged;
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
                return this.cageOuterMaterial;
            case INNER:
                return this.cageInnerMatterial;
            default:
                return this.cageOuterMaterial;
        }
    }

    public Location getCagePos()
    {
        return this.cagePosition;
    }

    public void clearHistory()
    {
        this.cageHistory.clear();
    }

    public void insertHistoryBlock(Location location, Material material)
    {
        this.cageHistory.add(new TFM_BlockData(location, material));
    }

    public void regenerateHistory()
    {
        for (TFM_BlockData blockdata : this.cageHistory)
        {
            blockdata.location.getBlock().setType(blockdata.material);
        }
    }

    private class TFM_BlockData
    {
        public Material material;
        public Location location;

        private TFM_BlockData(Location location, Material material)
        {
            this.location = location;
            this.material = material;
        }
    }

    public boolean isFrozen()
    {
        return this.isFrozen;
    }

    public void setFrozen(boolean fr)
    {
        this.isFrozen = fr;
    }

    public void resetMsgCount()
    {
        this.messageCount = 0;
    }

    public int incrementAndGetMsgCount()
    {
        return this.messageCount++;
    }

    public int incrementAndGetBlockDestroyCount()
    {
        return this.totalBlockDestroy++;
    }

    public void resetBlockDestroyCount()
    {
        this.totalBlockDestroy = 0;
    }

    public int incrementAndGetBlockPlaceCount()
    {
        return this.totalBlockPlace++;
    }

    public void resetBlockPlaceCount()
    {
        this.totalBlockPlace = 0;
    }

    public int incrementAndGetFreecamDestroyCount()
    {
        return this.freecamDestroyCount++;
    }

    public void resetFreecamDestroyCount()
    {
        this.freecamDestroyCount = 0;
    }

    public int incrementAndGetFreecamPlaceCount()
    {
        return this.freecamPlaceCount++;
    }

    public void resetFreecamPlaceCount()
    {
        this.freecamPlaceCount = 0;
    }

    public void enableMobThrower(EntityType mobThrowerCreature, double mobThrowerSpeed)
    {
        this.mobThrowerEnabled = true;
        this.mobThrowerEntity = mobThrowerCreature;
        this.mobThrowerSpeed = mobThrowerSpeed;
    }

    public void disableMobThrower()
    {
        this.mobThrowerEnabled = false;
    }

    public EntityType mobThrowerCreature()
    {
        return this.mobThrowerEntity;
    }

    public double mobThrowerSpeed()
    {
        return this.mobThrowerSpeed;
    }

    public boolean mobThrowerEnabled()
    {
        return this.mobThrowerEnabled;
    }

    public void enqueueMob(LivingEntity mob)
    {
        mobThrowerQueue.add(mob);
        if (mobThrowerQueue.size() > 4)
        {
            LivingEntity oldmob = mobThrowerQueue.remove(0);
            if (oldmob != null)
            {
                oldmob.damage(500.0);
            }
        }
    }

    public void startArrowShooter(TotalFreedomMod plugin)
    {
        this.stopArrowShooter();
        this.mp44ScheduleId = new ArrowShooter(this.player).runTaskTimer(plugin, 1L, 1L);
        this.mp44Firing = true;
    }

    public void stopArrowShooter()
    {
        if (this.mp44ScheduleId != null)
        {
            this.mp44ScheduleId.cancel();
            this.mp44ScheduleId = null;
        }
        this.mp44Firing = false;
    }

    private class ArrowShooter extends BukkitRunnable
    {
        private Player player;

        private ArrowShooter(Player player)
        {
            this.player = player;
        }

        @Override
        public void run()
        {
            Arrow shot = player.launchProjectile(Arrow.class);
            shot.setVelocity(shot.getVelocity().multiply(2.0));
        }
    }

    public void armMP44()
    {
        this.mp44Armed = true;
        this.stopArrowShooter();
    }

    public void disarmMP44()
    {
        this.mp44Armed = false;
        this.stopArrowShooter();
    }

    public boolean isMP44Armed()
    {
        return this.mp44Armed;
    }

    public boolean toggleMP44Firing()
    {
        this.mp44Firing = !this.mp44Firing;
        return mp44Firing;
    }

    public boolean isMuted()
    {
        return isMuted;
    }

    public void setMuted(boolean muted)
    {
        this.isMuted = muted;
    }

    public boolean isHalted()
    {
        return this.isHalted;
    }

    public void setHalted(boolean halted)
    {
        this.isHalted = halted;

        if (halted)
        {
            player.setOp(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.setFlying(false);
            TFM_EssentialsBridge.setNickname(player.getName(), player.getName());
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
        return this.lockupScheduleId;
    }

    public void setLockupScheduleID(BukkitTask id)
    {
        this.lockupScheduleId = id;
    }

    public void setLastMessage(String message)
    {
        this.lastMessage = message;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public void setAdminChat(boolean inAdminchat)
    {
        this.inAdminchat = inAdminchat;
    }

    public boolean inAdminChat()
    {
        return this.inAdminchat;
    }

    public boolean allCommandsBlocked()
    {
        return this.allCommandsBlocked;
    }

    public void setCommandsBlocked(boolean commandsBlocked)
    {
        this.allCommandsBlocked = commandsBlocked;
    }

    // If someone logs in to telnet or minecraft, and they are an admin, make sure that they are using a username that is associated with their IP.
    // After the check for this is done in TFM_PlayerListener, never change it elsewhere.
    public boolean isSuperadminIdVerified()
    {
        return this.verifiedSuperadminId;
    }

    // If someone logs in to telnet or minecraft, and they are an admin, make sure that they are using a username that is associated with their IP.
    // After the check for this is done in TFM_PlayerListener, never change it elsewhere.
    public void setSuperadminIdVerified(boolean verifiedSuperadminId)
    {
        this.verifiedSuperadminId = verifiedSuperadminId;
    }

    public String getLastCommand()
    {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand)
    {
        this.lastCommand = lastCommand;
    }

    public void setCommandSpy(boolean enabled)
    {
        this.cmdspyEnabled = enabled;
    }

    public boolean cmdspyEnabled()
    {
        return cmdspyEnabled;
    }

    public void setTag(String tag)
    {
        if (tag == null)
        {
            this.tag = null;
        }
        else
        {
            this.tag = TFM_Util.colorize(tag) + ChatColor.WHITE;
        }
    }

    public String getTag()
    {
        return this.tag;
    }
}
