package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
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

        FPlayer playerData = plugin.pl.getPlayer(playerSender);

        EntityType type = null;
        if (args.length >= 1)
        {
            if ("off".equals(args[0]))
            {
                playerData.disableMobThrower();
                msg("MobThrower is disabled.", ChatColor.GREEN);
                return true;
            }

            if (args[0].equalsIgnoreCase("list"))
            {
                StringBuilder sb = new StringBuilder();
                for (EntityType loop : EntityType.values())
                {
                    if (loop.isAlive())
                    {
                        sb.append(" ").append(DepreciationAggregator.getName_EntityType(loop));
                    }
                }
                msg("Supported mobs: " + sb.toString().trim(), ChatColor.GREEN);
                return true;
            }

            for (EntityType loopType : EntityType.values())
            {
                if (DepreciationAggregator.getName_EntityType(loopType).toLowerCase().equalsIgnoreCase(args[0]))
                {
                    type = loopType;
                    break;
                }
            }

            if (type == null)
            {
                msg(args[0] + " is not a supported mob type. Using a pig instead.", ChatColor.RED);
                msg("By the way, you can type /tossmob list to see all possible mobs.", ChatColor.RED);
                type = EntityType.PIG;
            }
        }

        double speed = 1.0;
        if (args.length >= 2)
        {
            try
            {
                speed = Double.parseDouble(args[1]);
            }
            catch (NumberFormatException nfex)
            {
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
        msg("MobThrower is enabled. Creature: " + type + " - Speed: " + speed + ".", ChatColor.GREEN);
        msg("Left click while holding a " + Material.BONE.toString() + " to throw mobs!", ChatColor.GREEN);
        msg("Type '/tossmob off' to disable.  -By Madgeek1450", ChatColor.GREEN);

        playerSender.getEquipment().setItemInMainHand(new ItemStack(Material.BONE, 1));

        return true;
    }
}
