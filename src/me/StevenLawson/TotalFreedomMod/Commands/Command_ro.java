package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, block_web_console = false, ignore_permissions = false)
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
                TFM_Util.playerMsg(sender, "Invalid block: " + args[0], ChatColor.RED);
                return true;
            }
        }

        int radius = 25;
        if (args.length >= 2)
        {
            try
            {
                radius = Math.max(1, Math.min(50, Integer.parseInt(args[1])));
            }
            catch (NumberFormatException ex)
            {
                TFM_Util.playerMsg(sender, "Invalid radius: " + args[1], ChatColor.RED);
                return true;
            }
        }

        Player target_player = null;
        if (args.length == 3)
        {
            try
            {
                target_player = getPlayer(args[2]);
            }
            catch (CantFindPlayerException ex)
            {
                TFM_Util.playerMsg(sender, ex.getMessage(), ChatColor.RED);
                return true;
            }
        }

        if (target_player == null)
        {
            for (Player p : server.getOnlinePlayers())
            {
                TFM_Util.replaceBlocks(p.getLocation(), from_material, Material.AIR, radius);
            }
        }
        else
        {
            TFM_Util.replaceBlocks(target_player.getLocation(), from_material, Material.AIR, radius);
        }

        return true;
    }
}
