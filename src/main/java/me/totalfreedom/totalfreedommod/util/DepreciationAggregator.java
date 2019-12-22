package me.totalfreedom.totalfreedommod.util;

import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.material.MaterialData;

@SuppressWarnings("deprecation")
public class DepreciationAggregator
{

    public static Block getTargetBlock(LivingEntity entity, HashSet<Material> transparent, int maxDistance)
    {
        return entity.getTargetBlock(transparent, maxDistance);
    }

    public static OfflinePlayer getOfflinePlayer(Server server, String name)
    {
        return server.getOfflinePlayer(name);
    }

    public static byte getData_MaterialData(MaterialData md)
    {
        return md.getData();
    }

    public static void setData_MaterialData(MaterialData md, byte data)
    {
        md.setData(data);
    }

    public static byte getData_Block(Block block)
    {
        return block.getData();
    }

    public static org.bukkit.material.Lever makeLeverWithData(byte data)
    {
        return new org.bukkit.material.Lever(Material.LEVER, data);
    }

    public static String getName_EntityType(EntityType et)
    {
        return et.getName();
    }
}