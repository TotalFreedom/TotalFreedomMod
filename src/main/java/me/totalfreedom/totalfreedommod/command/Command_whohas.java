package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "See who has a block and optionally smite.", usage = "/<command> <item> [smite]", aliases = "wh")
public class Command_whohas extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        final boolean doSmite = args.length >= 2 && "smite".equalsIgnoreCase(args[1]);

        final String materialName = args[0];
        Material material = Material.matchMaterial(materialName);
        if (material == null)
        {
            try
            {
                material = DepreciationAggregator.getMaterial(Integer.parseInt(materialName));
            }
            catch (NumberFormatException ex)
            {
            }
        }

        if (material == null)
        {
            msg("Invalid block: " + materialName, ChatColor.RED);
            return true;
        }

        final List<String> players = new ArrayList<>();

        for (final Player player : server.getOnlinePlayers())
        {
            if (player.getInventory().contains(material))
            {
                players.add(player.getName());
                if (doSmite && !plugin.al.isAdmin(player))
                {
                    Command_smite.smite(player);
                }
            }
        }

        if (players.isEmpty())
        {
            msg("There are no players with that item");
        }
        else
        {
            msg("Players with item " + material.name() + ": " + StringUtils.join(players, ", "));
        }

        return true;
    }
}
