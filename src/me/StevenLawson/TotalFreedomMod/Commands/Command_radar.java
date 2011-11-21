package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_RadarData;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_radar extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.NOT_FROM_CONSOLE);
            return true;
        }
        
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
            sender.sendMessage(ChatColor.YELLOW + "You are the only player in this world. (Forever alone...)");
            return true;
        }

        Collections.sort(radar_data, new TFM_RadarData());

        sender.sendMessage(ChatColor.YELLOW + "People nearby in " + sender_pos.getWorld().getName() + ":");

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
            sender.sendMessage(ChatColor.YELLOW + String.format("%s - %d",
                    i.player.getName(),
                    Math.round(i.distance)
                    ));
            
            if (--countmax <= 0)
            {
                break;
            }
        }

        return true;
    }
}
