package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "See who has a block and optionally smite.", usage = "/<command> <item> [smite]", aliases = "wh")
public class Command_whohas extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
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
                material = me.StevenLawson.TotalFreedomMod.TFM_DepreciationAggregator.getMaterial(Integer.parseInt(materialName));
            }
            catch (NumberFormatException ex)
            {
            }
        }

        if (material == null)
        {
            playerMsg("Invalid block: " + materialName, ChatColor.RED);
            return true;
        }

        final List<String> players = new ArrayList<String>();

        for (final Player player : server.getOnlinePlayers())
        {
            if (player.getInventory().contains(material))
            {
                players.add(player.getName());
                if (doSmite && !TFM_AdminList.isSuperAdmin(player))
                {
                    Command_smite.smite(player);
                }
            }
        }

        if (players.isEmpty())
        {
            playerMsg("There are no players with that item");
        }
        else
        {
            playerMsg("Players with item " + material.name() + ": " + StringUtils.join(players, ", "));
        }

        return true;
    }
}
