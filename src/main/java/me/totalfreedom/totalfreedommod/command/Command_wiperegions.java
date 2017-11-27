package me.totalfreedom.totalfreedommod.command;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Map;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandPermissions(level = Rank.TELNET_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Wipe all WorldGuard regions for a specified world.", usage = "/<command> <world>", aliases = "wiperegions")
public class Command_wiperegions extends FreedomCommand
{

    public WorldGuardPlugin getWorldGuard()
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin == null || !(plugin instanceof WorldGuardPlugin))
        {
            return null;
        }

        return (WorldGuardPlugin) plugin;
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (getWorldGuard() == null)
        {
            msg("WorldGuard is not installed.", ChatColor.GRAY);
            return true;
        }
        if (args.length != 1)
        {
            return false;
        }
        World world = server.getWorld(args[0]);
        if (world == null)
        {
            msg("World : \"" + args[0] + "\" not found.", ChatColor.GRAY);
            return true;
        }
        if (world.equals(plugin.wm.adminworld.getWorld()) && !plugin.rm.getRank(sender).isAtLeast(Rank.SENIOR_ADMIN))
        {
            msg("You do not have permission to wipe adminworld.", ChatColor.RED);
            return true;
        }
        RegionContainer container = getWorldGuard().getRegionContainer();
        RegionManager rm = container.get(world);
        if (rm != null)
        {
            Map<String, ProtectedRegion> regions = rm.getRegions();
            for (ProtectedRegion region : regions.values())
            {
                rm.removeRegion(region.getId());
            }
            FUtil.adminAction(sender.getName(), "Wiping regions for world: " + world.getName(), true);
            return true;
        }
        else
        {
            msg(ChatColor.RED + "No regions have been found for world: \"" + world.getName() + "\".");
            return true;
        }
    }

}
