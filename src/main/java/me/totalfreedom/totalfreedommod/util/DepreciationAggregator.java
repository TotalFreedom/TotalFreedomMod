package me.totalfreedom.totalfreedommod.util;

import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

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

    public static String getName_EntityType(EntityType et)
    {
        return et.getName();
    }
}
