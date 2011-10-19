package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_RadarData;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import org.bukkit.Bukkit;
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

        Player sender_player = Bukkit.getPlayerExact(sender.getName());
        Location sender_pos = sender_player.getLocation();
        String sender_world = sender_player.getWorld().getName();

        List<TFM_RadarData> radar_data = new ArrayList<TFM_RadarData>();

        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (sender_world.equals(p.getWorld().getName()) && !p.getName().equals(sender.getName()))
            {
                radar_data.add(new TFM_RadarData(p, sender_pos.distance(p.getLocation()), p.getLocation()));
            }
        }

        Collections.sort(radar_data, new TFM_RadarData());

        sender.sendMessage(ChatColor.YELLOW + "People nearby in " + sender_world + ":");

        int countmax = 5;
        if (args.length == 1)
        {
            try
            {
                countmax = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException nfex)
            {
            }
        }

        int count = 0;
        for (TFM_RadarData i : radar_data)
        {
            if (count++ > countmax)
            {
                break;
            }

            sender.sendMessage(ChatColor.YELLOW + String.format("%s - %d, Disguised: %s",
                    i.player.getName(),
                    Math.round(i.distance),
                    MobDisguiseAPI.isDisguised(i.player) ? "Yes" : "No"));
        }

        return true;
    }
}
