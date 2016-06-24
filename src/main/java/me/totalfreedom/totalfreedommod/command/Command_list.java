package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.BOTH)
@CommandParameters(description = "Lists the real names of all online players.", usage = "/<command> [-a | -i | -f]", aliases = "who")
public class Command_list extends FreedomCommand
{

    private static enum ListFilter
    {

        PLAYERS,
        ADMINS,
        FAMOUS_PLAYERS,
        IMPOSTORS;
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 1)
        {
            return false;
        }

        if (FUtil.isFromHostConsole(sender.getName()))
        {
            final List<String> names = new ArrayList<>();
            for (Player player : server.getOnlinePlayers())
            {
                names.add(player.getName());
            }
            msg("There are " + names.size() + "/" + server.getMaxPlayers() + " players online:\n" + StringUtils.join(names, ", "), ChatColor.WHITE);
            return true;
        }

        final ListFilter listFilter;
        if (args.length == 1)
        {
            switch (args[0])
            {
                case "-a":
                    listFilter = ListFilter.ADMINS;
                    break;
                case "-i":
                    listFilter = ListFilter.IMPOSTORS;
                    break;
                case "-f":
                    listFilter = ListFilter.FAMOUS_PLAYERS;
                    break;
                default:
                    return false;
            }
        }
        else
        {
            listFilter = ListFilter.PLAYERS;
        }

        final StringBuilder onlineStats = new StringBuilder();
        final StringBuilder onlineUsers = new StringBuilder();

        onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(server.getOnlinePlayers().size());
        onlineStats.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(server.getMaxPlayers());
        onlineStats.append(ChatColor.BLUE).append(" players online.");

        final List<String> names = new ArrayList<>();
        for (Player player : server.getOnlinePlayers())
        {
            if (listFilter == ListFilter.ADMINS && !plugin.al.isAdmin(player))
            {
                continue;
            }

            if (listFilter == ListFilter.IMPOSTORS && !plugin.al.isAdminImpostor(player))
            {
                continue;
            }

            if (listFilter == ListFilter.FAMOUS_PLAYERS && !ConfigEntry.FAMOUS_PLAYERS.getList().contains(player.getName().toLowerCase()))
            {
                continue;
            }

            Displayable display = plugin.rm.getDisplay(player);

            names.add(display.getColoredTag() + player.getName());
        }

        String playerType = listFilter == null ? "players" : listFilter.toString().toLowerCase().replace('_', ' ');

        onlineUsers.append("Connected ");
        onlineUsers.append(playerType + ": ");
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
