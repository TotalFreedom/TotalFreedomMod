package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Shows nearby people sorted by distance.", usage = "/<command> [range]")
public class Command_radar extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Location sender_pos = sender_p.getLocation();

        List<TFM_RadarData> radar_data = new ArrayList<TFM_RadarData>();

        for (Player player : sender_pos.getWorld().getPlayers())
        {
            if (!player.equals(sender_p))
            {
                try
                {
                    radar_data.add(new TFM_RadarData(player, sender_pos.distance(player.getLocation()), player.getLocation()));
                }
                catch (IllegalArgumentException ex)
                {
                }
            }
        }

        if (radar_data.isEmpty())
        {
            playerMsg("You are the only player in this world. (" + ChatColor.GREEN + "Forever alone..." + ChatColor.YELLOW + ")", ChatColor.YELLOW); //lol
            return true;
        }

        Collections.sort(radar_data, new TFM_RadarData());

        playerMsg("People nearby in " + sender_pos.getWorld().getName() + ":", ChatColor.YELLOW);

        int countmax = 5;
        if (args.length == 1)
        {
            try
            {
                countmax = Math.max(1, Math.min(64, Integer.parseInt(args[0])));
            }
            catch (NumberFormatException nfex)
            {
            }
        }

        for (TFM_RadarData i : radar_data)
        {
            playerMsg(String.format("%s - %d",
                    i.player.getName(),
                    Math.round(i.distance)), ChatColor.YELLOW);

            if (--countmax <= 0)
            {
                break;
            }
        }

        return true;
    }

    private class TFM_RadarData implements Comparator<TFM_RadarData>
    {
        public Player player;
        public double distance;
        public Location location;

        public TFM_RadarData(Player player, double distance, Location location)
        {
            this.player = player;
            this.distance = distance;
            this.location = location;
        }

        public TFM_RadarData()
        {
        }

        @Override
        public int compare(TFM_RadarData t1, TFM_RadarData t2)
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
}
