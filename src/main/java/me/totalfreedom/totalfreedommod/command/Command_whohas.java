package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "See who has an item and optionally clear the specified item.", usage = "/<command> <item> [clear]", aliases = "wh")
public class Command_whohas extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        final boolean doClear = args.length >= 2 && "clear".equalsIgnoreCase(args[1]);

        final String materialName = args[0];
        Material material = Material.matchMaterial(materialName);

        if (material == null)
        {
            msg("Invalid item: " + materialName, ChatColor.RED);
            return true;
        }

        final List<String> players = new ArrayList<>();

        for (final Player player : server.getOnlinePlayers())
        {
            if (!plugin.sl.isStaff(sender) && plugin.sl.isVanished(player.getName()))
            {
                continue;
            }
            if (player.getInventory().contains(material))
            {
                players.add(player.getName());
                if (plugin.sl.isStaff(sender))
                {
                    if (doClear && !plugin.sl.isStaff(player))
                    {
                        player.getInventory().remove(material);
                    }
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

    public static List<String> getAllMaterials()
    {
        List<String> names = new ArrayList<>();
        for (Material material : Material.values())
        {
            names.add(material.name());
        }
        return names;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            return getAllMaterials();
        }

        if (args.length == 2 && plugin.sl.isStaff(sender))
        {
            return Arrays.asList("clear");
        }

        return Collections.emptyList();
    }
}