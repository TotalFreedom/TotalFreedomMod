package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Wipe all WorldGuard regions for a specified world.", usage = "/<command> <world>")
public class Command_wiperegions extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.wgb.isEnabled())
        {
            msg("WorldGuard is not enabled.");
            return true;
        }

        if (args.length != 1)
        {
            return false;
        }

        World world = server.getWorld(args[0]);
        if (world == null)
        {
            msg("There is no world named \"" + args[0] + "\"", ChatColor.RED);
            return true;
        }
        if (world.equals(plugin.wm.adminworld.getWorld()))
        {
            checkRank(Rank.SENIOR_ADMIN);
        }

        int regionsWiped = plugin.wgb.wipeRegions(world);

        if (regionsWiped != 0)
        {
            FUtil.adminAction(sender.getName(), "Wiped all regions in " + world.getName(), true);
            msg("Wiped " + regionsWiped + " regions in " + world.getName());
            return true;
        }
        else
        {
            msg(ChatColor.RED + "No regions were found in \"" + world.getName() + "\"");
            return true;
        }
    }
}
