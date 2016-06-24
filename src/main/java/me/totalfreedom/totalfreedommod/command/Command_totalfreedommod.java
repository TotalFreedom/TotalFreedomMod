package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * See https://github.com/TotalFreedom/License - This file may not be edited or removed.
 */
@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows information about TotalFreedomMod", usage = "/<command>", aliases = "tfm")
public class Command_totalfreedommod extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TotalFreedomMod.BuildProperties build = TotalFreedomMod.build;
        msg("TotalFreedomMod for 'Total Freedom', the original all-op server.", ChatColor.GOLD);
        msg("Running on " + ConfigEntry.SERVER_NAME.getString() + ".", ChatColor.GOLD);
        msg("Created by Madgeek1450 and Prozza.", ChatColor.GOLD);
        msg(String.format("Version "
                + ChatColor.BLUE + "%s %s.%s " + ChatColor.GOLD + "("
                + ChatColor.BLUE + "%s" + ChatColor.GOLD + ")",
                build.codename,
                build.version,
                build.number,
                build.head), ChatColor.GOLD);
        msg(String.format("Compiled "
                + ChatColor.BLUE + "%s" + ChatColor.GOLD + " by "
                + ChatColor.BLUE + "%s",
                build.date,
                build.author), ChatColor.GOLD);
        msg("Visit " + ChatColor.AQUA + "http://github.com/TotalFreedom/TotalFreedomMod"
                + ChatColor.GREEN + " for more information.", ChatColor.GREEN);

        return true;
    }
}
