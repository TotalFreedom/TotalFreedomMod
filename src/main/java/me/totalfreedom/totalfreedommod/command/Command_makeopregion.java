package me.totalfreedom.totalfreedommod.command;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.HashMap;
import java.util.Map;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Make a region for an OP.", usage = "/<command> <playername> <name>", aliases = "mor")
public class Command_makeopregion extends FreedomCommand
{

    final Map<Flag<?>, Object> flags = new HashMap<Flag<?>, Object>() {{
        put(Flags.BLOCK_PLACE, StateFlag.State.DENY);
        put(Flags.BLOCK_BREAK, StateFlag.State.DENY);
        put(Flags.BUILD, StateFlag.State.DENY);
        put(Flags.PLACE_VEHICLE, StateFlag.State.DENY);
        put(Flags.DESTROY_VEHICLE, StateFlag.State.DENY);
        put(Flags.ENTITY_ITEM_FRAME_DESTROY, StateFlag.State.DENY);
        put(Flags.ENTITY_PAINTING_DESTROY, StateFlag.State.DENY);
    }};

    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (args.length < 2)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        String name = args[1];

        LocalSession session = plugin.web.getWorldEditPlugin().getSession(playerSender);

        Region selection = null;

        try
        {
            selection = session.getSelection(session.getSelectionWorld());
        }
        catch (IncompleteRegionException e)
        {
            msg("Please make a WorldEdit selection", ChatColor.RED);
            return true;
        }

        if (selection == null)
        {
            msg("Please make a WorldEdit selection", ChatColor.RED);
            return true;
        }

        ProtectedRegion region = new ProtectedCuboidRegion(name, selection.getMinimumPoint(), selection.getMaximumPoint());

        DefaultDomain owners = new DefaultDomain();
        owners.addPlayer(playerSender.getName());
        owners.addPlayer(player.getName());
        region.setOwners(owners);
        region.setFlags(flags);

        RegionManager regionManager = plugin.wgb.getWorldGuardPlugin().getRegionManager(playerSender.getWorld());

        regionManager.addRegion(region);

        msg("Successfully created the region '" + name + "' for " + player.getName(), ChatColor.GREEN);

        return true;
    }
}
