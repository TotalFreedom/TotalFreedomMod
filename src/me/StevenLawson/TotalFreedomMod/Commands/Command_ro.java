package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, block_host_console = false)
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

        Material from_material = Material.matchMaterial(args[0]);
        if (from_material == null)
        {
            try
            {
                from_material = Material.getMaterial(Integer.parseInt(args[0]));
            }
            catch (NumberFormatException ex)
            {
            }

            if (from_material == null)
            {
                playerMsg("Invalid block: " + args[0], ChatColor.RED);
                return true;
            }
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

        Player targetPlayer = null;
        if (args.length == 3)
        {
            try
            {
                targetPlayer = getPlayer(args[2]);
            }
            catch (PlayerNotFoundException ex)
            {
                playerMsg(ex.getMessage(), ChatColor.RED);
                return true;
            }
        }

        int affected = 0;

        if (targetPlayer == null)
        {
            TFM_Util.adminAction(sender.getName(), "Removing all " + from_material.name() + " within " + radius + " blocks of all players. Brace for lag...", senderIsConsole);
            for (Player player : server.getOnlinePlayers())
            {
                affected += TFM_Util.replaceBlocks(player.getLocation(), from_material, Material.AIR, radius);
            }
        }
        else
        {
            TFM_Util.adminAction(sender.getName(), "Removing all " + from_material.name() + " within " + radius + " blocks of " + targetPlayer.getName() + ".", senderIsConsole);
            affected += TFM_Util.replaceBlocks(targetPlayer.getLocation(), from_material, Material.AIR, radius);
        }

        TFM_Util.adminAction(sender.getName(), "Remove complete. " + affected + " blocks removed.", senderIsConsole);

        return true;
    }
}
