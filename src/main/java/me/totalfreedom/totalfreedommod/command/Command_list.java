package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.BOTH)
@CommandParameters(description = "Lists the real names of all online players.", usage = "/<command> [-a | -i | -f | -v]", aliases = "who")
public class Command_list extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
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
        onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(server.getOnlinePlayers().size() - Command_vanish.VANISHED.size())
                .append(ChatColor.BLUE)
                .append(" out of a maximum ")
                .append(ChatColor.RED)
                .append(server.getMaxPlayers())
                .append(ChatColor.BLUE)
                .append(" players online.");
        List<String> n = new ArrayList<>();
        for (Player p : server.getOnlinePlayers())
        {
            if (listFilter == ListFilter.ADMINS && !plugin.al.isAdmin(p))
            {
                continue;
            }
            if (listFilter == ListFilter.ADMINS && Command_vanish.VANISHED.contains(p))
            {
                continue;
            }
            if (listFilter == ListFilter.VANISHED_ADMINS && !Command_vanish.VANISHED.contains(p))
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
            if (listFilter == ListFilter.PLAYERS && Command_vanish.VANISHED.contains(p))
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
        FAMOUS_PLAYERS, 
        IMPOSTORS
    }
}
