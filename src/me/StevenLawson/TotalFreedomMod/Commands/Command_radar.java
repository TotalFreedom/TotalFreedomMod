package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_RadarData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
public class Command_radar extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Location sender_pos = sender_p.getLocation();

        List<TFM_RadarData> radar_data = new ArrayList<TFM_RadarData>();

        for (Player p : sender_pos.getWorld().getPlayers())
        {
            if (!p.equals(sender_p))
            {
                try
                {
                    radar_data.add(new TFM_RadarData(p, sender_pos.distance(p.getLocation()), p.getLocation()));
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
}
