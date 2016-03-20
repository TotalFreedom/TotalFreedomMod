package me.totalfreedom.totalfreedommod.command;

import java.util.Iterator;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.fun.Landminer.Landmine;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Set a landmine trap.", usage = "/<command>")
public class Command_landmine extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.LANDMINES_ENABLED.getBoolean())
        {
            msg("The landmine is currently disabled.", ChatColor.GREEN);
            return true;
        }

        if (!ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            msg("Explosions are currently disabled.", ChatColor.GREEN);
            return true;
        }

        double radius = 2.0;

        if (args.length >= 1)
        {
            if ("list".equals(args[0]))
            {
                final Iterator<Landmine> landmines = plugin.lm.getLandmines().iterator();
                while (landmines.hasNext())
                {
                    msg(landmines.next().toString());
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

        final Block landmine = playerSender.getLocation().getBlock().getRelative(BlockFace.DOWN);
        landmine.setType(Material.TNT);
        plugin.lm.add(new Landmine(landmine.getLocation(), playerSender, radius));

        msg("Landmine planted - Radius = " + radius + " blocks.", ChatColor.GREEN);

        return true;
    }

}
