package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Make an announcement", usage = "/<command> <mobtype> [amount]")
public class Command_spawnmob extends FreedomCommand
{

    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        EntityType type = null;
        for (EntityType loop : EntityType.values())
        {
            if (loop.getName().equalsIgnoreCase(args[0]))
            {
                type = loop;
                break;
            }
        }

        if (type == null)
        {
            msg("Unknown entity type: " + args[0], ChatColor.RED);
            return true;
        }

        if (!type.isSpawnable() || !type.isAlive())
        {
            msg("Can not spawn entity type: " + type.getName());
            return true;
        }

        int amount = 1;
        if (args.length > 1)
        {
            try
            {
                amount = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException nfex)
            {
                msg("Invalid amount: " + args[1], ChatColor.RED);
                return true;
            }
        }

        if (amount > 10 || amount < 1)
        {
            msg("Invalid amount: " + args[1] + ". Must be 1-10.", ChatColor.RED);
            return true;
        }

        Location l = playerSender.getLocation();
        World w = playerSender.getWorld();
        msg("Spawning " + amount + " of " + type.getName());

        for (int i = 0; i < amount; amount++)
        {
            w.spawnEntity(l, type);
        }
        return true;
    }

}
