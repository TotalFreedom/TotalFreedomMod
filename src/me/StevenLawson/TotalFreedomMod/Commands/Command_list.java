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
@CommandParameters(description = "Lists the real names of all online players.", usage = "/<command> [-a | -i]", aliases = "who")
public class Command_list extends TFM_Command
{
    private static enum ListFilter
    {
        ALL,
        ADMINS,
        IMPOSTORS;
    }

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 1)
        {
            return false;
        }

        if (TFM_Util.isFromHostConsole(sender.getName()))
        {
            final List<String> names = new ArrayList<String>();
            for (Player player : server.getOnlinePlayers())
            {
                names.add(player.getName());
            }
            playerMsg("There are " + names.size() + "/" + server.getMaxPlayers() + " players online:\n" + StringUtils.join(names, ", "), ChatColor.WHITE);
            return true;
        }

        final ListFilter listFilter;
        if (args.length == 1)
        {
            if ("-a".equals(args[0]))
            {
                listFilter = ListFilter.ADMINS;
            }
            else if ("-i".equals(args[0]))
            {
                listFilter = ListFilter.IMPOSTORS;
            }
            else
            {
                return false;
            }
        }
        else
        {
            listFilter = ListFilter.ALL;
        }

        final StringBuilder onlineStats = new StringBuilder();
        final StringBuilder onlineUsers = new StringBuilder();

        onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(server.getOnlinePlayers().size());
        onlineStats.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(server.getMaxPlayers());
        onlineStats.append(ChatColor.BLUE).append(" players online.");

        final List<String> names = new ArrayList<String>();
        for (Player player : server.getOnlinePlayers())
        {
            if (listFilter == ListFilter.ADMINS && !TFM_AdminList.isSuperAdmin(player))
            {
                continue;
            }

            if (listFilter == ListFilter.IMPOSTORS && !TFM_AdminList.isAdminImpostor(player))
            {
                continue;
            }

            names.add(TFM_PlayerRank.fromSender(player).getPrefix() + player.getName());
        }

        onlineUsers.append("Connected ");
        onlineUsers.append(listFilter == Command_list.ListFilter.ADMINS ? "admins: " : "players: ");
        onlineUsers.append(StringUtils.join(names, ChatColor.WHITE + ", "));

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
