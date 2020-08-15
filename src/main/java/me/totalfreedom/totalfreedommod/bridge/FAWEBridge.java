package me.totalfreedom.totalfreedommod.bridge;

import com.google.gson.Gson;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BaseBlock;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class FAWEBridge extends FreedomService
{
    private CoreProtectAPI api;
    private World world = null;
    private final Map<Map.Entry<String, EditSession>, Map<BlockVector3, String>> blocksBroken = new HashMap<>();
    private final Map<Map.Entry<String, EditSession>, Map.Entry<Pattern, List<BlockVector3>>> blocksPlaced = new HashMap<>();

    @Override
    public void onStart()
    {
        api = plugin.cpb.getCoreProtectAPI();

        /*
         * Iterates over blocks placed by GenerationCommands (in the EditSession) and adds them to the CoreProtect logs.
         */
        server.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                if (!(blocksBroken.isEmpty() && blocksPlaced.isEmpty()))
                {
                    // Send all broken blocks from the last ticks to the CoreProtect API.
                    Map.Entry<String, EditSession> playerAndSessionEntry = null;
                    for (Map.Entry<Map.Entry<String, EditSession>, Map<BlockVector3, String>> entry : blocksBroken.entrySet())
                    {
                        playerAndSessionEntry = entry.getKey();
                        Map<BlockVector3, String> dataAndVectorEntry = entry.getValue();
                        List<BlockVector3> blockVector3List = new ArrayList<>();
                        blockVector3List.addAll(dataAndVectorEntry.keySet()); // Deep clone the block vector to avoid a change later in the code.

                        for (BlockVector3 blockVector3 : blockVector3List)
                        {
                            if (blockVector3 != null)
                            {
                                EditSession editSession = playerAndSessionEntry.getValue();
                                World world = server.getWorld(editSession.getWorld().getName());
                                Location location = new Location(world, blockVector3.getX(), blockVector3.getY(), blockVector3.getZ());
                                BlockData blockData = server.createBlockData(dataAndVectorEntry.get(blockVector3));
                                api.logRemoval(playerAndSessionEntry.getKey(), location, blockData.getMaterial(), blockData);
                            }
                        }
                    }

                    // Clear after broken blocks have been updated.
                    blocksBroken.values().clear();
                    blocksBroken.putIfAbsent(playerAndSessionEntry, new HashMap<>());

                    // Send all blocks placed to the CoreProtect API (except from the air as it's only a broken block).
                    for (Map.Entry<Map.Entry<String, EditSession>, Map.Entry<Pattern, List<BlockVector3>>> entry : blocksPlaced.entrySet())
                    {
                        playerAndSessionEntry = entry.getKey();
                        Map.Entry<Pattern, List<BlockVector3>> patternAndListEntry = entry.getValue();
                        Pattern pattern = patternAndListEntry.getKey();
                        List<BlockVector3> blockVector3List = new ArrayList<>();
                        blockVector3List.addAll(patternAndListEntry.getValue()); // Deep clone the block vector to avoid a change later in the code.

                        for (BlockVector3 blockVector3 : blockVector3List)
                        {
                            if (blockVector3 != null && !pattern.apply(blockVector3).getBlockType().getMaterial().isAir())
                            {
                                World world = server.getWorld(playerAndSessionEntry.getValue().getWorld().getName());
                                Location location = new Location(world, blockVector3.getX(), blockVector3.getY(), blockVector3.getZ());
                                BaseBlock block = pattern.apply(blockVector3);
                                Material material = Material.getMaterial(block.getBlockType().getId().replaceFirst("minecraft:", "").toUpperCase());
                                api.logPlacement(playerAndSessionEntry.getKey(), location, material, material.createBlockData());
                            }
                        }
                    }

                    blocksPlaced.values().forEach(collection -> collection.getValue().clear());
                }
            }
        }, 0L, 40L);
    }

    @Override
    public void onStop()
    {

    }


    public void logBlockEdit(String playerName, EditSession editSession, Pattern pattern, BlockVector3 blockVector3)
    {
        // Cache the world used for the next iterations to come.
        if (world == null || !world.getName().equals(editSession.getWorld().getName()))
        {
            world = server.getWorld(editSession.getWorld().getName());
        }

        Map.Entry<String, EditSession> playerAndSessionEntry = new AbstractMap.SimpleEntry(playerName, editSession);
        Block block = world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());

        // Add the broken block to CoreProtect if it's not air.
        if (!block.getType().isAir())
        {
            String blockData = block.getBlockData().getAsString();
            blockData = new Gson().fromJson(new Gson().toJson(blockData), blockData.getClass()); // Overwrite original with deep clones.
            blockVector3 = new Gson().fromJson(new Gson().toJson(blockVector3), blockVector3.getClass()); // Overwrite original with deep clones.
            blocksBroken.putIfAbsent(playerAndSessionEntry, new HashMap<>());
            blocksBroken.get(playerAndSessionEntry).put(blockVector3, blockData);
        }

        // Add the placed block to CoreProtect if it's not air.
        if (!pattern.apply(blockVector3).getBlockType().getMaterial().isAir())
        {
            blocksPlaced.putIfAbsent(playerAndSessionEntry, new AbstractMap.SimpleEntry<>(pattern, new ArrayList<>()));
            blocksPlaced.get(playerAndSessionEntry).getValue().add(new Gson().fromJson(new Gson().toJson(blockVector3), blockVector3.getClass()));
        }
    }

    public void logBlockEdits(String playerName, EditSession editSession, Region region, Pattern pattern)
    {
        // Add the broken blocks to CoreProtect.
        if (world == null || !world.getName().equals(editSession.getWorld().getName()))
        {
            world = server.getWorld(editSession.getWorld().getName());
        }
        List<Block> blocks = new ArrayList<>();

        for (BlockVector3 blockVector3 : region)
        {
            blocks.add(world.getBlockAt(blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ()));
        }

        logBlockEdit(playerName, editSession, pattern, blocks);
    }

    public void logBlockEdit(String playerName, EditSession editSession, Pattern pattern, List<Block> blocks)
    {
        Map.Entry<String, EditSession> playerAndSessionEntry = new AbstractMap.SimpleEntry(playerName, editSession);

        server.getScheduler().scheduleSyncDelayedTask(plugin, () ->
        {
            for (Block block : blocks)
            {
                BlockVector3 blockVector3 = BlockVector3.at(block.getX(), block.getY(), block.getZ());

                // Add the broken block to CoreProtect if it's not air.
                if (!block.getType().isAir())
                {
                    api.logRemoval(playerAndSessionEntry.getKey(), block.getLocation(), block.getType(), block.getBlockData());
                }

                // Add the placed block to CoreProtect if it's not air.
                BaseBlock baseBlock = pattern.apply(blockVector3);
                if (!baseBlock.getBlockType().getMaterial().isAir())
                {
                    Material material = Material.getMaterial(baseBlock.getBlockType().getId().replaceFirst("minecraft:", "").toUpperCase());
                    api.logPlacement(playerAndSessionEntry.getKey(), block.getLocation(), material, material.createBlockData());
                }
            }
        }, 0L);
    }
}