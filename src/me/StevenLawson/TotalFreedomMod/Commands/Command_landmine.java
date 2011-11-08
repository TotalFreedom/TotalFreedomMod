package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_LandmineData;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_landmine extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.NOT_FROM_CONSOLE);
        }
        else if (!TotalFreedomMod.landminesEnabled)
        {
            sender.sendMessage(ChatColor.GREEN + "The landmine is currently disabled.");
        }
        else if (!TotalFreedomMod.allowExplosions)
        {
            sender.sendMessage(ChatColor.GREEN + "Explosions are currently disabled.");
        }
        else if (sender.isOp())
        {
            double radius = 2.0;
            if (args.length >= 1)
            {
                try
                {
                    radius = Math.max(2.0, Math.min(6.0, Double.parseDouble(args[0])));
                }
                catch (NumberFormatException ex)
                {
                }
            }
            
            Block landmine = sender_p.getLocation().getBlock().getRelative(BlockFace.DOWN);
            landmine.setType(Material.TNT);
            TFM_LandmineData.landmines.add(new TFM_LandmineData(landmine.getLocation(), sender_p, radius));
            
            sender.sendMessage(ChatColor.GREEN + "Landmine planted. Radius: " + radius + " blocks.");
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
