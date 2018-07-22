package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.apache.commons.lang3.EnumUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Spawn an entity.", usage = "/<command> <entitytype> [amount]", aliases="spawnentity")
public class Command_spawnmob extends FreedomCommand
{

    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0 && args[0].equalsIgnoreCase("list"))
        {
            List<EntityType> types = EnumUtils.getEnumList(EntityType.class);
            String typeList = StringUtils.join(types, ", ").toLowerCase();
            msg(typeList);
            return true;
        }

        if (args.length < 1)
        {
            return false;
        }

        EntityType type = null;
        for (EntityType loop : EntityType.values())
        {
            if (loop != null && loop.name().equalsIgnoreCase(args[0]))
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
            msg("Can not spawn entity type: " + type.name().toLowerCase());
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

        Location l = playerSender.getTargetBlock((Set<Material>) null, 30).getLocation().add(0, 1, 0);
        World w = playerSender.getWorld();
        msg("Spawning " + amount + " " + type.name().toLowerCase() + (amount > 1 ? "s." : "."));

        for (int i = 0; i < amount; i++)
        {
            w.spawnEntity(l, type);
        }
        return true;
    }

}
