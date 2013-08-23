package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public final class TFM_AdminWorld extends TFM_CustomWorld
{
    private static final long CACHE_CLEAR_FREQUENCY = 30L * 1000L; //30 seconds, milliseconds
    private static final long TP_COOLDOWN_TIME = 500L; //0.5 seconds, milliseconds
    private static final String GENERATION_PARAMETERS = TFM_ConfigEntry.FLATLANDS_GENERATION_PARAMS.getString();
    private static final String WORLD_NAME = "adminworld";
    //
    private final Map<Player, Long> teleportCooldown = new HashMap<Player, Long>();
    private final Map<CommandSender, Boolean> accessCache = new HashMap<CommandSender, Boolean>();
    //
    private Long cacheLastCleared = null;
    private Map<Player, Player> guestList = new HashMap<Player, Player>();

    private TFM_AdminWorld()
    {
    }

    @Override
    public void sendToWorld(Player player)
    {
        if (!canAccessWorld(player))
        {
            return;
        }

        super.sendToWorld(player);
    }

    @Override
    protected World generateWorld()
    {
        WorldCreator worldCreator = new WorldCreator(WORLD_NAME);
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generator(new CleanroomChunkGenerator(GENERATION_PARAMETERS));

        World world = Bukkit.getServer().createWorld(worldCreator);

        world.setSpawnFlags(false, false);
        world.setSpawnLocation(0, 50, 0);

        Block welcomeSignBlock = world.getBlockAt(0, 50, 0);
        welcomeSignBlock.setType(Material.SIGN_POST);
        org.bukkit.block.Sign welcomeSign = (org.bukkit.block.Sign) welcomeSignBlock.getState();

        org.bukkit.material.Sign signData = (org.bukkit.material.Sign) welcomeSign.getData();
        signData.setFacingDirection(BlockFace.NORTH);

        welcomeSign.setLine(0, ChatColor.GREEN + "AdminWorld");
        welcomeSign.setLine(1, ChatColor.DARK_GRAY + "---");
        welcomeSign.setLine(2, ChatColor.YELLOW + "Spawn Point");
        welcomeSign.setLine(3, ChatColor.DARK_GRAY + "---");
        welcomeSign.update();

        TFM_GameRuleHandler.commitGameRules();

        return world;
    }

    public void addGuest(Player guest, Player supervisor)
    {
        if (TFM_SuperadminList.isUserSuperadmin(supervisor))
        {
            guestList.put(guest, supervisor);
            wipeAccessCache();
        }
    }

    public Player removeGuest(Player guest)
    {
        Player player = guestList.remove(guest);
        wipeAccessCache();
        return player;
    }

    public Player removeGuest(String partialName)
    {
        partialName = partialName.toLowerCase().trim();
        Iterator<Player> it = guestList.values().iterator();
        while (it.hasNext())
        {
            Player player = it.next();
            if (player.getName().toLowerCase().trim().contains(partialName))
            {
                return removeGuest(player);
            }
        }
        return null;
    }

    public String guestListToString()
    {
        List<String> output = new ArrayList<String>();
        Iterator<Map.Entry<Player, Player>> it = guestList.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<Player, Player> entry = it.next();
            Player player = entry.getKey();
            Player supervisor = entry.getValue();
            output.add(player.getName() + " (Supervisor: " + supervisor.getName() + ")");
        }
        return StringUtils.join(output, ", ");
    }

    public void purgeGuestList()
    {
        guestList.clear();
        wipeAccessCache();
    }

    public boolean validateMovement(PlayerMoveEvent event)
    {
        World world;
        try
        {
            world = getWorld();
        }
        catch (Exception ex)
        {
            return true;
        }

        if (world != null && event.getTo().getWorld() == world)
        {
            final Player player = event.getPlayer();
            if (!canAccessWorld(player))
            {
                Long lastTP = teleportCooldown.get(player);
                long currentTimeMillis = System.currentTimeMillis();
                if (lastTP == null || lastTP.longValue() + TP_COOLDOWN_TIME <= currentTimeMillis)
                {
                    teleportCooldown.put(player, currentTimeMillis);
                    TFM_Log.info(player.getName() + " attempted to access the AdminWorld.");
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        }
                    }.runTaskLater(TotalFreedomMod.plugin, 1L);
                }
                event.setCancelled(true);
                return false;
            }
        }

        return true;
    }

    public void wipeAccessCache()
    {
        cacheLastCleared = System.currentTimeMillis();
        accessCache.clear();
    }

    public boolean canAccessWorld(final Player player)
    {
        long currentTimeMillis = System.currentTimeMillis();
        if (cacheLastCleared == null || cacheLastCleared.longValue() + CACHE_CLEAR_FREQUENCY <= currentTimeMillis)
        {
            cacheLastCleared = currentTimeMillis;
            accessCache.clear();
        }

        Boolean cached = accessCache.get(player);
        if (cached == null)
        {
            boolean canAccess = TFM_SuperadminList.isUserSuperadmin(player);
            if (!canAccess)
            {
                Player supervisor = guestList.get(player);
                canAccess = supervisor != null && supervisor.isOnline() && TFM_SuperadminList.isUserSuperadmin(supervisor);
                if (!canAccess)
                {
                    guestList.remove(player);
                }
            }
            cached = canAccess;
            accessCache.put(player, cached);
        }
        return cached;
    }

    public static TFM_AdminWorld getInstance()
    {
        return TFM_AdminWorldHolder.INSTANCE;
    }

    private static class TFM_AdminWorldHolder
    {
        private static final TFM_AdminWorld INSTANCE = new TFM_AdminWorld();
    }
}
