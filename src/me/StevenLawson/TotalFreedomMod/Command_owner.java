package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerRank;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Checks if the owner is online or offline.", usage = "/<command> [-a | -i]", aliases = "who")
public class Command_owner extends TFM_Command

    @Override
    public boolean run(CommandSender sender, Command cmd, String label, String[] args) {
           Player slime = Bukkit.getServer().getPlayer("markbyron");
           Player player = (Player) sender;
        if (slime != null)
        {
            player.sendMessage(ChatColor.GREEN + "The owner, MarkByron, is " + ChatColor.GREEN + "online" + ChatColor.GREEN + "!");
            return true;

        }
        else if (slime == null)
        {
            player.sendMessage(ChatColor.GREEN + "The owner, MarkByron, is " + ChatColor.DARK_RED + "offline" + ChatColor.GREEN + "!");
            return true;
        }

        else
        {

        }
        return true;

    }
    }
