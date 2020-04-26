package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.admin.AdminList;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.BOTH)
@CommandParameters(description = "Lists the real names of all online players.", usage = "/<command> [-a | -i | -f | -v]", aliases = "who,lsit")
public class Command_list extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (args.length > 1)
        {
            return false;
        }
        if (FUtil.isFromHostConsole(sender.getName()))
        {
            List<String> names = new ArrayList<>();
            for (Player player : server.getOnlinePlayers())
            {
                names.add(player.getName());
            }
            msg("There are " + names.size() + "/" + server.getMaxPlayers() + " players online:\n" + StringUtils.join(names, ", "), ChatColor.WHITE);
            return true;
        }
        ListFilter listFilter;
        if (args.length == 1)
        {
            String s = args[0];
            switch (s)
            {
                case "-a":
                {
                    listFilter = ListFilter.ADMINS;
                    break;
                }
                case "-v":
                {
                    checkRank(Rank.SUPER_ADMIN);
                    listFilter = ListFilter.VANISHED_ADMINS;
                    break;
                }
                case "-t":
                {
                    checkRank(Rank.TELNET_ADMIN);
                    listFilter = ListFilter.TELNET_SESSIONS;
                    break;
                }
                case "-i":
                {
                    listFilter = ListFilter.IMPOSTORS;
                    break;
                }
                case "-f":
                {
                    listFilter = ListFilter.FAMOUS_PLAYERS;
                    break;
                }
                default:
                {
                    return false;
                }
            }
        }
        else
        {
            listFilter = ListFilter.PLAYERS;
        }
        StringBuilder onlineStats = new StringBuilder();
        StringBuilder onlineUsers = new StringBuilder();

        List<String> n = new ArrayList<>();

        if (listFilter == ListFilter.TELNET_SESSIONS && plugin.al.isAdmin(sender) && plugin.al.getAdmin(sender).getRank().isAtLeast(Rank.TELNET_ADMIN))
        {
            List<Admin> connectedAdmins = plugin.btb.getConnectedAdmins();
            onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(connectedAdmins.size())
                    .append(ChatColor.BLUE)
                    .append(" admins connected to telnet.");
            for (Admin admin : connectedAdmins)
            {
                n.add(plugin.rm.getDisplay(admin).getColoredTag() + admin.getName());
            }
        }
        else
        {
            onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(server.getOnlinePlayers().size() - plugin.al.vanished.size())
                    .append(ChatColor.BLUE)
                    .append(" out of a maximum ")
                    .append(ChatColor.RED)
                    .append(server.getMaxPlayers())
                    .append(ChatColor.BLUE)
                    .append(" players online.");
            for (Player p : server.getOnlinePlayers())
            {
                if (listFilter == ListFilter.ADMINS && !plugin.al.isAdmin(p))
                {
                    continue;
                }
                if (listFilter == ListFilter.ADMINS && AdminList.vanished.contains(p))
                {
                    continue;
                }
                if (listFilter == ListFilter.VANISHED_ADMINS && !AdminList.vanished.contains(p))
                {
                    continue;
                }
                if (listFilter == ListFilter.IMPOSTORS && !plugin.al.isAdminImpostor(p))
                {
                    continue;
                }
                if (listFilter == ListFilter.FAMOUS_PLAYERS && !ConfigEntry.FAMOUS_PLAYERS.getList().contains(p.getName().toLowerCase()))
                {
                    continue;
                }
                if (listFilter == ListFilter.PLAYERS && AdminList.vanished.contains(p))
                {
                    continue;
                }

                final Displayable display = plugin.rm.getDisplay(p);
                if (!senderIsConsole && plugin.al.isAdmin(playerSender) && plugin.al.getAdmin(playerSender).getOldTags())
                {
                    n.add(getOldPrefix(display) + p.getName());
                }
                else
                {
                    n.add(display.getColoredTag() + p.getName());
                }
            }
        }
        String playerType = listFilter.toString().toLowerCase().replace('_', ' ');
        onlineUsers.append("Connected ")
                .append(playerType)
                .append(": ")
                .append(StringUtils.join(n, ChatColor.WHITE + ", "));
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
        n.clear();
        return true;
    }

    public String getOldPrefix(Displayable display)
    {
        ChatColor color = display.getColor();

        if (color.equals(ChatColor.AQUA))
        {
            color = ChatColor.GOLD;
        }
        else if (color.equals(ChatColor.GOLD))
        {
            color = ChatColor.LIGHT_PURPLE;
        }

        String prefix = "[" + display.getAbbr() + "]";

        return color + prefix;
    }

    private enum ListFilter
    {
        PLAYERS,
        ADMINS,
        VANISHED_ADMINS,
        TELNET_SESSIONS,
        FAMOUS_PLAYERS,
        IMPOSTORS
    }
}