package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.BOTH)
@CommandParameters(description = "Lists the real names of all online players.", usage = "/<command> [-a | -i | -v]", aliases = "who")
public class Command_list extends FreedomCommand
{

    private static enum ListFilter
    {
        PLAYERS,
        ADMINS,
        VANISHED_ADMINS,
        IMPOSTORS;
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 1)
        {
            return false;
        }
        final ListFilter listFilter;
        if (args.length == 1)
        {
            switch (args[0])
            {
                case "-a":
                    listFilter = ListFilter.ADMINS;
                    break;
                case "-v":
                    listFilter = ListFilter.VANISHED_ADMINS;
                    break;
                case "-i":
                    listFilter = ListFilter.IMPOSTORS;
                    break;
                default:
                    return false;
            }
        }
        else
        {
            listFilter = ListFilter.PLAYERS;
        }

        if (listFilter == ListFilter.VANISHED_ADMINS && !isAdmin(playerSender))
        {
            msg("You can't view vanished admins.");
            return true;
        }
        final StringBuilder onlineStats = new StringBuilder();
        final StringBuilder onlineUsers = new StringBuilder();
        int vanished = 0;
        for (Player player : server.getOnlinePlayers())
        {
            FPlayer fPlayer = plugin.pl.getPlayer(player);
            if (fPlayer.isVanish())
            {
                vanished += 1;
            }
        }
        onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(server.getOnlinePlayers().size() - vanished);
        onlineStats.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(server.getMaxPlayers());
        onlineStats.append(ChatColor.BLUE).append(" players online.");

        final List<String> names = new ArrayList<>();
        for (Player player : server.getOnlinePlayers())
        {
            FPlayer fPlayer = plugin.pl.getPlayer(player);
            if (listFilter == ListFilter.ADMINS && !isAdmin(player))
            {
                continue;
            }

            if (listFilter == ListFilter.ADMINS && fPlayer.isVanish())
            {
                continue;
            }

            if (listFilter == ListFilter.VANISHED_ADMINS && !fPlayer.isVanish())
            {
                continue;
            }

            if (listFilter == ListFilter.IMPOSTORS && !plugin.al.isAdminImpostor(player))
            {
                continue;
            }

            if (listFilter == ListFilter.PLAYERS && fPlayer.isVanish())
            {
                continue;
            }
            Displayable display = plugin.rm.getDisplay(player);

            names.add(display.getColoredTag() + player.getName());
        }

        String playerType = listFilter == null ? "players" : listFilter.toString().toLowerCase().replace('_', ' ');

        onlineUsers.append("Connected ");
        onlineUsers.append(playerType);
        onlineUsers.append(": ");
        onlineUsers.append(StringUtils.join(names, ChatColor.WHITE + ", "));

        if (isConsole())
        {
            sender.sendMessage(ChatColor.stripColor(onlineStats.toString()));
            sender.sendMessage(ChatColor.stripColor(onlineUsers.toString()));
        }
        else
        {
            sender.sendMessage(onlineStats.toString());
            sender.sendMessage(onlineUsers.toString());
        }
        names.clear();
        return true;
    }
}
