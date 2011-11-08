package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TFM_LandmineData
{
    public static List<TFM_LandmineData> landmines = new ArrayList<TFM_LandmineData>();
    
    public Location landmine_pos;
    public Player player;
    public double radius;

    public TFM_LandmineData(Location landmine_pos, Player player, double radius)
    {
        this.landmine_pos = landmine_pos;
        this.player = player;
        this.radius = radius;
    }
}
