package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Throw a mob in the direction you are facing when you left click with a stick.",
        usage = "/<command> <mobtype [speed] | off | list>")
public class Command_tossmob extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.TOSSMOB_ENABLED.getBoolean())
        {
            msg("Tossmob is currently disabled.");
            return true;
        }

        if (args.length == 0)
        {
            return false;
        }

        FPlayer playerData = plugin.pl.getPlayer(playerSender);

        EntityType type = null;
        if (args.length >= 1)
        {
            if (args[0].equalsIgnoreCase("off"))
            {
                playerData.disableMobThrower();
                msg("MobThrower is disabled.", ChatColor.GREEN);
                return true;
            }

            if (args[0].equalsIgnoreCase("list"))
            {
                msg("Supported mobs: " + getAllMobNames(), ChatColor.GREEN);
                return true;
            }

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

            if (!Groups.MOB_TYPES.contains(type))
            {
                msg(FUtil.formatName(type.name()) + " is an entity, however it is not a mob.", ChatColor.RED);
                return true;
            }
        }

        double speed = 1.0;
        if (args.length >= 2)
        {
            try
            {
                speed = Double.parseDouble(args[1]);
            }
            catch (NumberFormatException ex)
            {
                msg("The input provided is not a valid integer.");
                return true;
            }
        }

        if (speed < 1.0)
        {
            speed = 1.0;
        }
        else if (speed > 5.0)
        {
            speed = 5.0;
        }

        playerData.enableMobThrower(type, speed);
        msg("MobThrower is enabled. Mob: " + type + " - Speed: " + speed + ".", ChatColor.GREEN);
        msg("Left click while holding a " + Material.BONE.toString() + " to throw mobs!", ChatColor.GREEN);
        msg("Type '/tossmob off' to disable. -By Madgeek1450", ChatColor.GREEN);

        playerSender.getEquipment().setItemInMainHand(new ItemStack(Material.BONE, 1));
        return true;
    }
    public static List<String> getAllMobNames()
    {
        List<String> names = new ArrayList<>();
        for (EntityType entityType : Groups.MOB_TYPES)
        {
            names.add(entityType.name());
        }
        return names;
    }
}