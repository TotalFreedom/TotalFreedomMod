package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TFM_AdminWorld
{
    private static final String GENERATION_PARAMETERS = "16,stone,32,dirt,1,grass";
    private static final String ADMINWORLD_NAME = "adminworld";
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

        if (adminWorld == null || !Bukkit.getWorlds().contains(adminWorld))
        {
            generateWorld();
        }

        player.teleport(adminWorld.getSpawnLocation());
    }

    public void validateTeleport(PlayerTeleportEvent event)
    {
        if (adminWorld == null)
        {
            return;
        }

        if (event.getTo().getWorld() == adminWorld)
        {
            if (!TFM_SuperadminList.isUserSuperadmin(event.getPlayer()))
            {
                event.setCancelled(true);
            }
        }
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
        Sign welcomeSign = (Sign) welcomeSignBlock.getState();
        welcomeSign.setLine(0, "AdminWorld");
        welcomeSign.setLine(1, "");
        welcomeSign.setLine(2, "");
        welcomeSign.setLine(3, "");

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
