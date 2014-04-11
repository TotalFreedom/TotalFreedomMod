package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Set a landmine trap.", usage = "/<command>")
public class Command_landmine extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!TFM_ConfigEntry.LANDMINES_ENABLED.getBoolean())
        {
            playerMsg("The landmine is currently disabled.", ChatColor.GREEN);
        }
        else if (!TFM_ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            playerMsg("Explosions are currently disabled.", ChatColor.GREEN);
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

            playerMsg("Landmine planted. Radius: " + radius + " blocks.", ChatColor.GREEN);
        }

        return true;
    }

    public static class TFM_LandmineData
    {
        public static List<TFM_LandmineData> landmines = new ArrayList<TFM_LandmineData>();
        public Location location;
        public Player player;
        public double radius;

        public TFM_LandmineData(Location landmine_pos, Player player, double radius)
        {
            super();
            this.location = landmine_pos;
            this.player = player;
            this.radius = radius;
        }
    }
}
