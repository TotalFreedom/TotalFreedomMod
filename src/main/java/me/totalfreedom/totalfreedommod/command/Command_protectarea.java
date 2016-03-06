package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.ProtectArea;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(
        description = "Protect areas so that only superadmins can directly modify blocks in those areas. WorldEdit and other such plugins might bypass this.",
        usage = "/<command> <list | clear | remove <label> | add <label> <radius>>")
public class Command_protectarea extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.PROTECTAREA_ENABLED.getBoolean())
        {
            msg("Protected areas are currently disabled in the TotalFreedomMod configuration.");
            return true;
        }

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("list"))
            {
                msg("Protected Areas: " + StringUtils.join(plugin.pa.getProtectedAreaLabels(), ", "));
            }
            else if (args[0].equalsIgnoreCase("clear"))
            {
                plugin.pa.clearProtectedAreas();

                msg("Protected Areas Cleared.");
            }
            else
            {
                return false;
            }

            return true;
        }
        else if (args.length == 2)
        {
            if ("remove".equals(args[0]))
            {
                plugin.pa.removeProtectedArea(args[1]);

                msg("Area removed. Protected Areas: " + StringUtils.join(plugin.pa.getProtectedAreaLabels(), ", "));
            }
            else
            {
                return false;
            }

            return true;
        }
        else if (args.length == 3)
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                if (senderIsConsole)
                {
                    msg("You must be in-game to set a protected area.");
                    return true;
                }

                Double radius;
                try
                {
                    radius = Double.parseDouble(args[2]);
                }
                catch (NumberFormatException nfex)
                {
                    msg("Invalid radius.");
                    return true;
                }

                if (radius > ProtectArea.MAX_RADIUS || radius < 0.0D)
                {
                    msg("Invalid radius. Radius must be a positive value less than " + ProtectArea.MAX_RADIUS + ".");
                    return true;
                }

                plugin.pa.addProtectedArea(args[1], playerSender.getLocation(), radius);

                msg("Area added. Protected Areas: " + StringUtils.join(plugin.pa.getProtectedAreaLabels(), ", "));
            }
            else
            {
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
