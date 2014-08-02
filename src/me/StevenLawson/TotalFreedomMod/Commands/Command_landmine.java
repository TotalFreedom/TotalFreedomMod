package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.Iterator;
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
            return true;
        }

        if (!TFM_ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            playerMsg("Explosions are currently disabled.", ChatColor.GREEN);
            return true;
        }

        double radius = 2.0;

        if (args.length >= 1)
        {
            if ("list".equalsIgnoreCase(args[0]))
            {
                final Iterator<TFM_LandmineData> landmines = TFM_LandmineData.landmines.iterator();
                while (landmines.hasNext())
                {
                    playerMsg(landmines.next().toString());
                }
                return true;
            }

            try
            {
                radius = Math.max(2.0, Math.min(6.0, Double.parseDouble(args[0])));
            }
            catch (NumberFormatException ex)
            {
            }
        }

        final Block landmine = sender_p.getLocation().getBlock().getRelative(BlockFace.DOWN);
        landmine.setType(Material.TNT);
        TFM_LandmineData.landmines.add(new TFM_LandmineData(landmine.getLocation(), sender_p, radius));

        playerMsg("Landmine planted - Radius = " + radius + " blocks.", ChatColor.GREEN);

        return true;
    }

    public static class TFM_LandmineData
    {
        public static final List<TFM_LandmineData> landmines = new ArrayList<TFM_LandmineData>();
        public final Location location;
        public final Player player;
        public final double radius;

        public TFM_LandmineData(Location location, Player player, double radius)
        {
            this.location = location;
            this.player = player;
            this.radius = radius;
        }

        @Override
        public String toString()
        {
            return this.location.toString() + ", " + this.radius + ", " + this.player.getName();
        }
    }
}
