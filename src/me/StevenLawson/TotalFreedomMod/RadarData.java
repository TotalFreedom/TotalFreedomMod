package me.StevenLawson.TotalFreedomMod;

import java.util.Comparator;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RadarData implements Comparator<RadarData>
{
    Player player;
    double distance;
    Location location;

    public RadarData(Player inplayer, double indistance, Location inlocation)
    {
        this.player = inplayer;
        this.distance = indistance;
        this.location = inlocation;
    }

    public RadarData()
    {
    }

    @Override
    public int compare(RadarData t1, RadarData t2)
    {
        if (t1.distance > t2.distance)
        {
            return 1;
        }
        else if (t1.distance < t2.distance)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
