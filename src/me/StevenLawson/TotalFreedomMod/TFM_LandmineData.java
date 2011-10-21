package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TFM_LandmineData
{
    public Location landmine_pos;
    public Player player;
    
    public TFM_LandmineData(Location landmine_pos, Player player)
    {
        this.landmine_pos = landmine_pos;
        this.player = player;
    }
}
