package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "See who has a block and optional smite", usage = "/<command> <item> [smite]", aliases="wh")
public class Command_whohas extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        boolean smite = false;

        if (args.length != 1)
        {
            if (args.length == 2 && args[1].equals("smite"))
            {
                smite = true;
            }
            else
            {
                return false;
            }
        }

        Material material = Material.matchMaterial(args[0]);
        
        if (material == null)
        {
            try
            {
                material = Material.getMaterial(Integer.parseInt(args[0]));
            }
            catch (NumberFormatException ex)
            {
            }

            if (material == null)
            {
                playerMsg("Invalid block: " + args[0], ChatColor.RED);
                return true;
            }
        }

        List<String> players = new ArrayList<String>();

        for (Player p : server.getOnlinePlayers())
        {
            if (p.getInventory().contains(material))
            {
                players.add(p.getName());
                if (smite & !TFM_SuperadminList.isUserSuperadmin(p))
                {
                    Command_smite.smite(p);
                }
            }
        }

        if (players.isEmpty())
        {
             playerMsg("There are no players with that item");
        }
        else
        {
            playerMsg("Players with item " + material.name() + ": "+ StringUtils.join(players, ", "));
        }

        return true;

    }
}
