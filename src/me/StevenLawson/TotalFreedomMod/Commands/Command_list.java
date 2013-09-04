package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Lists the real names of all online players.", usage = "/<command>", aliases = "who")
public class Command_list extends TFM_Command
{
    private static enum ListFilter
    {
        SHOW_ALL, SHOW_ADMINS
    }

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (TFM_Util.isFromHostConsole(sender.getName()))
        {
            List<String> player_names = new ArrayList<String>();
            for (Player player : server.getOnlinePlayers())
            {
                player_names.add(player.getName());
            }
            playerMsg("There are " + player_names.size() + "/" + server.getMaxPlayers() + " players online:\n" + StringUtils.join(player_names, ", "), ChatColor.WHITE);
            return true;
        }

        ListFilter listFilter = ListFilter.SHOW_ALL;
        if (args.length >= 1)
        {
            if (args[0].equalsIgnoreCase("-a"))
            {
                listFilter = ListFilter.SHOW_ADMINS;
            }
        }

        StringBuilder onlineStats = new StringBuilder();
        StringBuilder onlineUsers = new StringBuilder();

        onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(server.getOnlinePlayers().length);
        onlineStats.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(server.getMaxPlayers());
        onlineStats.append(ChatColor.BLUE).append(" players online.");

        List<String> player_names = new ArrayList<String>();
        for (Player player : server.getOnlinePlayers())
        {
            boolean userSuperadmin = TFM_SuperadminList.isUserSuperadmin(player);

            if (listFilter == ListFilter.SHOW_ADMINS && !userSuperadmin)
            {
                continue;
            }

            String prefix = "";

            if (userSuperadmin)
            {
                if (TFM_SuperadminList.isSeniorAdmin(player))
                {
                    prefix = (ChatColor.LIGHT_PURPLE + "[SrA]");
                }
                else
                {
                    prefix = (ChatColor.GOLD + "[SA]");
                }

                if (TFM_Util.DEVELOPERS.contains(player.getName()))
                {
                    prefix = (ChatColor.DARK_PURPLE + "[Dev]");
                }

                if (player.getName().equals("markbyron"))
                {
                    prefix = (ChatColor.BLUE + "[Owner]");
                }
            }
            else
            {
                if (player.isOp())
                {
                    prefix = (ChatColor.RED + "[OP]");
                }
            }

            player_names.add(prefix + player.getName() + ChatColor.WHITE);
        }

        onlineUsers.append("Connected ").append(listFilter == ListFilter.SHOW_ADMINS ? "admins" : "players").append(": ").append(StringUtils.join(player_names, ", "));

        if (senderIsConsole)
        {
            sender.sendMessage(ChatColor.stripColor(onlineStats.toString()));
            sender.sendMessage(ChatColor.stripColor(onlineUsers.toString()));
        }
        else
        {
            sender.sendMessage(onlineStats.toString());
            sender.sendMessage(onlineUsers.toString());
        }

        return true;
    }
}
