package me.totalfreedom.totalfreedommod.command;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Map;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandPermissions(level = Rank.TELNET_ADMIN, source = SourceType.ONLY_IN_GAME, blockHostConsole = false)
@CommandParameters(description = "Wipe all Worldguard regions.", usage = "/<command> <world>", aliases = "wiperegions")
public class Command_wipeworldguardregions extends FreedomCommand
{

    public WorldGuardPlugin getWorldGuard()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

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
            msg("WorldGuard is not installed.", ChatColor.DARK_GRAY);
            return true;
        }
        if (!(args.length == 1))
        {
            return false;
        }
        World world = Bukkit.getWorld(args[0]);
        if (world == null)
        {
            msg("World not found.", ChatColor.DARK_GRAY);
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
            sender.sendMessage(ChatColor.RED + "There hasn't been any regions made for world \"" + world.getName() + "\".");
            return true;
        }
    }

}
