package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_cage extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            if (args.length == 0)
            {
                return false;
            }

            Player p;
            List<Player> matches = Bukkit.matchPlayer(args[0]);
            if (matches.isEmpty())
            {
                sender.sendMessage(ChatColor.GRAY + "Can't find user " + args[0]);
                return true;
            }
            else
            {
                p = matches.get(0);
            }

            TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p, plugin);

            Material cage_material_outer = Material.GLASS;
            Material cage_material_inner = Material.AIR;
            if (args.length >= 2)
            {
                if (args[1].equalsIgnoreCase("end") || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("stop"))
                {
                    playerdata.setCaged(false);
                    playerdata.regenerateHistory();
                    playerdata.clearHistory();
                    return true;
                }
                else
                {
                    cage_material_outer = Material.matchMaterial(args[1]);
                    if (cage_material_outer == null)
                    {
                        cage_material_outer = Material.GLASS;
                    }
                }
            }

            if (args.length >= 3)
            {
                if (args[2].equalsIgnoreCase("water"))
                {
                    cage_material_inner = Material.STATIONARY_WATER;
                }
                else if (args[2].equalsIgnoreCase("lava"))
                {
                    cage_material_inner = Material.STATIONARY_LAVA;
                }
            }

            Location target_pos = p.getLocation().add(0, 1, 0);
            playerdata.setCaged(true, target_pos, cage_material_outer, cage_material_inner);
            playerdata.regenerateHistory();
            playerdata.clearHistory();
            TFM_Util.buildHistory(target_pos, 2, playerdata);
            TFM_Util.generateCube(target_pos, 2, playerdata.getCageMaterial(TFM_UserInfo.CageLayer.OUTER));
            TFM_Util.generateCube(target_pos, 1, playerdata.getCageMaterial(TFM_UserInfo.CageLayer.INNER));

            p.setGameMode(GameMode.SURVIVAL);

            TFM_Util.tfm_broadcastMessage(sender.getName() + " caged " + p.getName() + "!", ChatColor.YELLOW);
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
