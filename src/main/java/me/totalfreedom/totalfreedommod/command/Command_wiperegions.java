package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TELNET_ADMIN, source = SourceType.BOTH)
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
            msg("World : \"" + args[0] + "\" not found.");
            return true;
        }
        if (world.equals(plugin.wm.adminworld.getWorld()))
        {
            checkRank(Rank.SENIOR_ADMIN);
        }
        if (plugin.wgb.wipeRegions(world))
        {
            FUtil.adminAction(sender.getName(), "Wiping regions for world: " + world.getName(), true);
            return true;
        }
        else
        {
            msg(ChatColor.RED + "No regions were found in: \"" + world.getName() + "\".");
            return true;
        }
    }
}
