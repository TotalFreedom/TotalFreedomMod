package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = false)
@CommandParameters(description = "Remove all blocks of a certain type in the radius of certain players.", usage = "/<command> <block> [radius (default=50)] [player]")
public class Command_ro extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1 || args.length > 3)
        {
            return false;
        }

        final List<Material> materials = new ArrayList<Material>();

        for (String materialName : (args[0].contains(",") ? args[0].split(",") : new String[]
        {
            args[0]
        }))
        {
            Material fromMaterial = Material.matchMaterial(materialName);
            if (fromMaterial == null)
            {
                try
                {
                    fromMaterial = Material.getMaterial(Integer.parseInt(materialName));
                }
                catch (NumberFormatException ex)
                {
                }
            }

            if (fromMaterial == null)
            {
                playerMsg("Invalid block: " + materialName, ChatColor.RED);
                return true;
            }

            materials.add(fromMaterial);
        }

        int radius = 20;
        if (args.length >= 2)
        {
            try
            {
                radius = Math.max(1, Math.min(50, Integer.parseInt(args[1])));
            }
            catch (NumberFormatException ex)
            {
                playerMsg("Invalid radius: " + args[1], ChatColor.RED);
                return true;
            }
        }

        final Player targetPlayer;
        if (args.length == 3)
        {
            targetPlayer = getPlayer(args[2]);
            if (targetPlayer == null)
            {
                playerMsg(TotalFreedomMod.PLAYER_NOT_FOUND);
                return true;
            }
        }
        else
        {
            targetPlayer = null;
        }

        final String names = StringUtils.join(materials, ", ");

        int affected = 0;
        if (targetPlayer == null)
        {
            TFM_Util.adminAction(sender.getName(), "Removing all " + names + " within " + radius + " blocks of all players... Brace for lag!", false);

            for (Material material : materials)
            {
                for (Player player : server.getOnlinePlayers())
                {
                    affected += TFM_Util.replaceBlocks(player.getLocation(), material, Material.AIR, radius);
                }
            }
        }
        else
        {
            for (Material material : materials)
            {
                TFM_Util.adminAction(sender.getName(), "Removing all " + names + " within " + radius + " blocks of " + targetPlayer.getName(), false);
                affected += TFM_Util.replaceBlocks(targetPlayer.getLocation(), material, Material.AIR, radius);
            }
        }

        TFM_Util.adminAction(sender.getName(), "Remove complete! " + affected + " blocks removed.", false);


        return true;
    }
}
