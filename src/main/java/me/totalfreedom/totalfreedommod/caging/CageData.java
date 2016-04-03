package me.totalfreedom.totalfreedommod.caging;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;

public class CageData
{

    private final FPlayer fPlayer;
    //
    private final List<BlockData> cageHistory = new ArrayList<>();
    //
    @Getter
    private boolean caged = false;
    @Getter
    private Location location;
    @Getter
    private Material outerMaterial = Material.GLASS;
    @Getter
    private Material innerMaterial = Material.AIR;

    public CageData(FPlayer player)
    {
        this.fPlayer = player;
    }

    public void setCaged(boolean cage)
    {
        if (cage)
        {
            cage(fPlayer.getPlayer().getLocation(), Material.GLASS, Material.GLASS);
        }
        else
        {
            this.caged = false;
            regenerateHistory();
            clearHistory();
        }

    }

    public void cage(Location location, Material outer, Material inner)
    {
        if (isCaged())
        {
            setCaged(false);
        }

        this.caged = true;
        this.location = location;
        this.outerMaterial = outer;
        this.innerMaterial = inner;

        buildHistory(location, 2, fPlayer);
        regenerate();
    }

    public void regenerate()
    {

        if (!caged
                || location == null
                || outerMaterial == null
                || innerMaterial == null)
        {
            return;
        }

        generateHollowCube(location, 2, outerMaterial);
        generateCube(location, 1, innerMaterial);
    }

    // TODO: EventHandlerize this?
    public void playerJoin()
    {
        if (!isCaged())
        {
            return;
        }

        cage(fPlayer.getPlayer().getLocation(), outerMaterial, innerMaterial);
    }

    public void playerQuit()
    {
        regenerateHistory();
        clearHistory();
    }

    public void clearHistory()
    {
        cageHistory.clear();
    }

    private void insertHistoryBlock(Location location, Material material)
    {
        cageHistory.add(new BlockData(location, material));
    }

    private void regenerateHistory()
    {
        for (BlockData blockdata : this.cageHistory)
        {
            blockdata.location.getBlock().setType(blockdata.material);
        }
    }

    private void buildHistory(Location location, int length, FPlayer playerdata)
    {
        final Block center = location.getBlock();
        for (int xOffset = -length; xOffset <= length; xOffset++)
        {
            for (int yOffset = -length; yOffset <= length; yOffset++)
            {
                for (int zOffset = -length; zOffset <= length; zOffset++)
                {
                    final Block block = center.getRelative(xOffset, yOffset, zOffset);
                    insertHistoryBlock(block.getLocation(), block.getType());
                }
            }
        }
    }

    // Util methods
    public static void generateCube(Location location, int length, Material material)
    {
        final Block center = location.getBlock();
        for (int xOffset = -length; xOffset <= length; xOffset++)
        {
            for (int yOffset = -length; yOffset <= length; yOffset++)
            {
                for (int zOffset = -length; zOffset <= length; zOffset++)
                {
                    final Block block = center.getRelative(xOffset, yOffset, zOffset);
                    if (block.getType() != material)
                    {
                        block.setType(material);
                    }
                }
            }
        }
    }

    public static void generateHollowCube(Location location, int length, Material material)
    {
        final Block center = location.getBlock();
        for (int xOffset = -length; xOffset <= length; xOffset++)
        {
            for (int yOffset = -length; yOffset <= length; yOffset++)
            {
                for (int zOffset = -length; zOffset <= length; zOffset++)
                {
                    // Hollow
                    if (Math.abs(xOffset) != length && Math.abs(yOffset) != length && Math.abs(zOffset) != length)
                    {
                        continue;
                    }

                    final Block block = center.getRelative(xOffset, yOffset, zOffset);

                    if (material != Material.SKULL)
                    {
                        // Glowstone light
                        if (material != Material.GLASS && xOffset == 0 && yOffset == 2 && zOffset == 0)
                        {
                            block.setType(Material.GLOWSTONE);
                            continue;
                        }

                        block.setType(material);
                    }
                    else // Darth mode
                    {
                        if (Math.abs(xOffset) == length && Math.abs(yOffset) == length && Math.abs(zOffset) == length)
                        {
                            block.setType(Material.GLOWSTONE);
                            continue;
                        }

                        block.setType(Material.SKULL);
                        final Skull skull = (Skull) block.getState();
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner("Prozza");
                        skull.update();
                    }
                }
            }
        }
    }

    private static class BlockData
    {

        public Material material;
        public Location location;

        private BlockData(Location location, Material material)
        {
            this.location = location;
            this.material = material;
        }
    }
}
