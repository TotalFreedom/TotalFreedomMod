package me.StevenLawson.TotalFreedomMod;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_AdminWorld
{
    private static final long CACHE_CLEAR_FREQUENCY = 30L * 1000L; //30 seconds, milliseconds
    private static final long TP_COOLDOWN_TIME = 500L; //0.5 seconds, milliseconds
    private static final String GENERATION_PARAMETERS = TFM_ConfigEntry.FLATLANDS_GENERATION_PARAMS.getString();
    private static final String ADMINWORLD_NAME = "adminworld";
    //
    private final Map<Player, Long> teleportCooldown = new HashMap<Player, Long>();
    private final Map<CommandSender, Boolean> superadminCache = new HashMap<CommandSender, Boolean>();
    //
    private Long cacheLastCleared = null;
    private World adminWorld = null;

    private TFM_AdminWorld()
    {
    }

    public void sendToAdminWorld(Player player)
    {
        if (!TFM_SuperadminList.isUserSuperadmin(player))
        {
            return;
        }

        player.teleport(getAdminWorld().getSpawnLocation());
    }

    public boolean validateMovement(PlayerMoveEvent event)
    {
        if (adminWorld != null)
        {
            if (event.getTo().getWorld() == adminWorld)
            {
                final Player player = event.getPlayer();
                if (!cachedIsUserSuperadmin(player))
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
        }
        return true;
    }

    public World getAdminWorld()
    {
        if (adminWorld == null || !Bukkit.getWorlds().contains(adminWorld))
        {
            generateWorld();
        }

        return adminWorld;
    }

    public void wipeSuperadminCache()
    {
        cacheLastCleared = System.currentTimeMillis();
        superadminCache.clear();
    }

    private boolean cachedIsUserSuperadmin(CommandSender user)
    {
        long currentTimeMillis = System.currentTimeMillis();
        if (cacheLastCleared == null || cacheLastCleared.longValue() + CACHE_CLEAR_FREQUENCY <= currentTimeMillis)
        {
            cacheLastCleared = currentTimeMillis;
            superadminCache.clear();
        }

        Boolean cached = superadminCache.get(user);
        if (cached == null)
        {
            cached = TFM_SuperadminList.isUserSuperadmin(user);
            superadminCache.put(user, cached);
        }
        return cached;
    }

    private void generateWorld()
    {
        WorldCreator adminWorldCreator = new WorldCreator(ADMINWORLD_NAME);
        adminWorldCreator.generateStructures(false);
        adminWorldCreator.type(WorldType.NORMAL);
        adminWorldCreator.environment(World.Environment.NORMAL);
        adminWorldCreator.generator(new CleanroomChunkGenerator(GENERATION_PARAMETERS));

        adminWorld = Bukkit.getServer().createWorld(adminWorldCreator);

        adminWorld.setSpawnFlags(false, false);
        adminWorld.setSpawnLocation(0, 50, 0);

        Block welcomeSignBlock = adminWorld.getBlockAt(0, 50, 0);
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
