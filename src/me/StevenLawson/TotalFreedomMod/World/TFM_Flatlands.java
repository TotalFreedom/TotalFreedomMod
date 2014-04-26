package me.StevenLawson.TotalFreedomMod.World;

import me.StevenLawson.TotalFreedomMod.World.TFM_CustomWorld;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import java.io.File;
import me.StevenLawson.TotalFreedomMod.TFM_GameRuleHandler;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class TFM_Flatlands extends TFM_CustomWorld
{
    private static final String GENERATION_PARAMETERS = TFM_ConfigEntry.FLATLANDS_GENERATE_PARAMS.getString();
    private static final String WORLD_NAME = "flatlands";

    private TFM_Flatlands()
    {
    }

    @Override
    protected World generateWorld()
    {
        if (!TFM_ConfigEntry.FLATLANDS_GENERATE.getBoolean())
        {
            return null;
        }

        wipeFlatlandsIfFlagged();

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

        welcomeSign.setLine(0, ChatColor.GREEN + "Flatlands");
        welcomeSign.setLine(1, ChatColor.DARK_GRAY + "---");
        welcomeSign.setLine(2, ChatColor.YELLOW + "Spawn Point");
        welcomeSign.setLine(3, ChatColor.DARK_GRAY + "---");
        welcomeSign.update();

        TFM_GameRuleHandler.commitGameRules();

        return world;
    }

    public static void wipeFlatlandsIfFlagged()
    {
        boolean doFlatlandsWipe = false;
        try
        {
            doFlatlandsWipe = TFM_Util.getSavedFlag("do_wipe_flatlands");
        }
        catch (Exception ex)
        {
        }

        if (doFlatlandsWipe)
        {
            if (Bukkit.getServer().getWorld("flatlands") == null)
            {
                TFM_Log.info("Wiping flatlands.");
                TFM_Util.setSavedFlag("do_wipe_flatlands", false);
                FileUtils.deleteQuietly(new File("./flatlands"));
            }
            else
            {
                TFM_Log.severe("Can't wipe flatlands, it is already loaded.");
            }
        }
    }

    public static TFM_Flatlands getInstance()
    {
        return TFM_FlatlandsHolder.INSTANCE;
    }

    private static class TFM_FlatlandsHolder
    {
        private static final TFM_Flatlands INSTANCE = new TFM_Flatlands();
    }
}
