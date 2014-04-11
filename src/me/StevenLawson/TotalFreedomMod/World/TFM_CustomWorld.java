package me.StevenLawson.TotalFreedomMod.World;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class TFM_CustomWorld
{
    private World world;

    protected abstract World generateWorld();

    public void sendToWorld(Player player)
    {
        try
        {
            player.teleport(getWorld().getSpawnLocation());
        }
        catch (Exception ex)
        {
            player.sendMessage(ex.getMessage());
        }
    }

    public final World getWorld() throws Exception
    {
        if (world == null || !Bukkit.getWorlds().contains(world))
        {
            world = generateWorld();
        }

        if (world == null)
        {
            throw new Exception("World not loaded.");
        }

        return world;
    }
}
