/*
 * Cleanroom Generator
 * Copyright (C) 2011 nvx
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

package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import static java.lang.System.arraycopy;

public class CleanroomChunkGenerator extends ChunkGenerator
{
    private byte[] chunk = new byte[32768];

    public CleanroomChunkGenerator()
    {
        this("64,stone");
    }

    public CleanroomChunkGenerator(String id)
    {
        byte[] layer = new byte[128];
        if (id != null)
        {
            try
            {
                int y = 0;

                if ((id.length() > 0) && (id.charAt(0) == '.')) // Is the first character a '.'? If so, skip bedrock generation.
                {
                    id = id.substring(1); // Skip bedrock then and remove the .
                } else // Guess not, bedrock at layer0 it is then.
                {
                    layer[y++] = (byte)Material.BEDROCK.getId();
                }

                if (id.length() > 0)
                {
                    String tokens[] = id.split("[,]");

                    if ((tokens.length % 2) != 0) throw new Exception();

                    for (int i = 0; i < tokens.length; i += 2)
                    {
                        int height = Integer.parseInt(tokens[i]);
                        if ((height <= 0) || (height > 127))
                        {
                            System.out.println("Invalid height '"  + tokens[i] + "'. Using 64 instead.");
                            height = 64;
                        }

                        if ((height + y) > 127)
                        {
                            System.out.println("Maximum height reached, ignoring additional layers.");
                            break;
                        }

                        Material mat = Material.matchMaterial(tokens[i + 1]);
                        if (mat == null)
                        {
                            try
                            {
                                // Mabe it's an integer?
                                mat = Material.getMaterial(Integer.parseInt(tokens[i + 1]));
                            } catch (Exception e)
                            {
                                // Well, I guess it wasn't an integer after all... Awkward...
                            }

                            if (mat == null)
                            {
                                System.out.println("Invalid Block ID '" + tokens[i + 1] + "'. Defaulting to stone.");
                                mat = Material.STONE;
                            }
                        }

                        if (!mat.isBlock())
                        {
                            System.out.println("Error, '" + tokens[i + 1] + "' is not a block. Defaulting to stone.");
                            mat = Material.STONE;
                        }

                        Arrays.fill(layer, y, y + height, (byte)mat.getId());
                        y += height;
                    }
                }
            } catch(Exception e)
            {
                System.out.println("Error parsing CleanroomGenerator ID '" + id + "'. using defaults '64,1': " + e.toString());
                e.printStackTrace();
                layer[0] = (byte)Material.BEDROCK.getId();
                Arrays.fill(layer, 1, 65, (byte)Material.STONE.getId());
                Arrays.fill(layer, 65, 128, (byte)Material.AIR.getId()); // Just in case...
            }
        } else
        {
            layer[0] = (byte)Material.BEDROCK.getId();
            Arrays.fill(layer, 1, 65, (byte)Material.STONE.getId());
        }

        for (int xz = 0; xz < 256; xz++)
        {
            arraycopy(layer, 0, chunk, xz * 128, layer.length);
        }
    }

    public byte[] generate(World world, Random random, int cx, int cz)
    {
        return chunk.clone(); // Can't get more efficient than that...
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world)
    {
        // This is the default, but just in case default populators change to stock minecraft populators by default...
        return new ArrayList<BlockPopulator>();
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
