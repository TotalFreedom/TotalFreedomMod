package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Shows nearby people sorted by distance.", usage = "/<command> [range]")
public class Command_radar extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Location playerSenderos = playerSender.getLocation();

        List<TFM_RadarData> radar_data = new ArrayList<>();

        for (Player player : playerSenderos.getWorld().getPlayers())
        {
            if (!player.equals(playerSender))
            {
                try
                {
                    radar_data.add(new TFM_RadarData(player, playerSenderos.distance(player.getLocation()), player.getLocation()));
                }
                catch (IllegalArgumentException ex)
                {
                }
            }
        }

        if (radar_data.isEmpty())
        {
            msg("You are the only player in this world. (" + ChatColor.GREEN + "Forever alone..." + ChatColor.YELLOW + ")", ChatColor.YELLOW); //lol
            return true;
        }

        Collections.sort(radar_data, new TFM_RadarData());

        msg("People nearby in " + playerSenderos.getWorld().getName() + ":", ChatColor.YELLOW);

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
            msg(String.format("%s - %d",
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
