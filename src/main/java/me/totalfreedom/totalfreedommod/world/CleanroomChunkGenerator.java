/*
 * Cleanroom Generator
 * Copyright (C) 2011-2012 nvx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.totalfreedom.totalfreedommod.world;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import static java.lang.System.arraycopy;

public class CleanroomChunkGenerator extends ChunkGenerator
{
    private Logger log = Logger.getLogger("Minecraft");

    private Material[] materials;

    public CleanroomChunkGenerator()
    {
        this("16,stone,32,dirt,1,grass_block");
    }

    public CleanroomChunkGenerator(String id)
    {
        if (id != null)
        {
            try
            {
                int y = 0;

                materials = new Material[128]; // Default to 128, will be resized later if required
                materials[y++] = Material.BEDROCK;

                if (id.length() > 0)
                {
                    String tokens[] = id.split("[,]");

                    if ((tokens.length % 2) != 0)
                    {
                        throw new Exception();
                    }

                    for (int i = 0; i < tokens.length; i += 2)
                    {
                        int height = Integer.parseInt(tokens[i]);
                        if (height <= 0)
                        {
                            log.warning("[CleanroomGenerator] Invalid height '" + tokens[i] + "'. Using 64 instead.");
                            height = 64;
                        }

                        String materialTokens[] = tokens[i + 1].split("[:]", 2);

                        if (materialTokens.length == 2)
                        {
                            log.warning("[CleanroomGenerator] Data values are no longer supported in 1.13. Defaulting to the base material for " + materialTokens[0]);
                        }

                        Material mat = Material.matchMaterial(materialTokens[0]);
                        if (mat == null)
                        {
                            log.warning("[CleanroomGenerator] Invalid Block ID '" + materialTokens[0] + "'. Defaulting to stone. (Integer IDs were removed in 1.13)");
                            mat = Material.STONE;
                        }

                        if (!mat.isBlock())
                        {
                            log.warning("[CleanroomGenerator] Error, '" + materialTokens[0] + "' is not a block. Defaulting to stone.");
                            mat = Material.STONE;
                        }

                        if (y + height > materials.length)
                        {
                            Material[] newMaterials = new Material[Math.max(y + height, materials.length * 2)];

                            arraycopy(materials, 0, newMaterials, 0, y);
                            materials = newMaterials;
                        }

                        Arrays.fill(materials, y, y + height, mat);
                        y += height;
                    }
                }

                // Trim to size
                if (materials.length > y)
                {
                    Material[] newMaterials = new Material[y];
                    arraycopy(materials, 0, newMaterials, 0, y);
                    materials = newMaterials;
                }
            }
            catch (Exception e)
            {
                log.severe("[CleanroomGenerator] Error parsing CleanroomGenerator ID '" + id + "'. using defaults '64,1': " + e.toString());
                e.printStackTrace();

                materials = new Material[65];
                materials[0] = Material.BEDROCK;
                Arrays.fill(materials, 1, 65, Material.STONE);
            }
        }
        else
        {
            materials = new Material[65];
            materials[0] = Material.BEDROCK;
            Arrays.fill(materials, 1, 65, Material.STONE);
        }
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome)
    {
        int maxHeight = world.getMaxHeight();
        if (materials.length > maxHeight)
        {
            log.warning("[CleanroomGenerator] Error, chunk height " + materials.length + " is greater than the world max height (" + maxHeight + "). Trimming to world max height.");
            Material[] newMaterials = new Material[maxHeight];
            arraycopy(materials, 0, newMaterials, 0, maxHeight);
            materials = newMaterials;
        }

        ChunkData result = createChunkData(world);

        for (int y = 0; y < materials.length; y++)
        {
            result.setRegion(0, y, 0, 16, y+1, 16, materials[y]);
        }
      

        return result;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random)
    {
        if (!world.isChunkLoaded(0, 0))
        {
            world.loadChunk(0, 0);
        }

        if ((world.getHighestBlockYAt(0, 0) <= 0) && (world.getBlockAt(0, 0, 0).getType() == Material.AIR)) // SPACE!
        {
            return new Location(world, 0, 64, 0); // Lets allow people to drop a little before hitting the void then shall we?
        }

        return new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
    }
}
