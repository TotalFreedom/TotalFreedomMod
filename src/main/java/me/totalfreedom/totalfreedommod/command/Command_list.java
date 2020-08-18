package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffList;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.BOTH)
@CommandParameters(description = "Lists the real names of all online players.", usage = "/<command> [-s | -i | -f | -v]", aliases = "who,lsit")
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
                case "-s":
                {
                    listFilter = ListFilter.STAFF;
                    break;
                }
                case "-v":
                {
                    checkRank(Rank.TRIAL_MOD);
                    listFilter = ListFilter.VANISHED_STAFF;
                    break;
                }
                case "-t":
                {
                    checkRank(Rank.MOD);
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

        if (listFilter == ListFilter.TELNET_SESSIONS && plugin.sl.isStaff(sender) && plugin.sl.getAdmin(sender).getRank().isAtLeast(Rank.MOD))
        {
            List<StaffMember> connectedStaffMembers = plugin.btb.getConnectedAdmins();
            onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(connectedStaffMembers.size())
                    .append(ChatColor.BLUE)
                    .append(" staff connected to telnet.");
            for (StaffMember staffMember : connectedStaffMembers)
            {
                n.add(plugin.rm.getDisplay(staffMember).getColoredTag() + staffMember.getName());
            }
        }
        else
        {
            onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(server.getOnlinePlayers().size() - StaffList.vanished.size())
                    .append(ChatColor.BLUE)
                    .append(" out of a maximum ")
                    .append(ChatColor.RED)
                    .append(server.getMaxPlayers())
                    .append(ChatColor.BLUE)
                    .append(" players online.");
            for (Player p : server.getOnlinePlayers())
            {
                if (listFilter == ListFilter.STAFF && !plugin.sl.isStaff(p))
                {
                    continue;
                }
                if (listFilter == ListFilter.STAFF && plugin.sl.isVanished(p.getName()))
                {
                    continue;
                }
                if (listFilter == ListFilter.VANISHED_STAFF && !plugin.sl.isVanished(p.getName()))
                {
                    continue;
                }
                if (listFilter == ListFilter.IMPOSTORS && !plugin.sl.isStaffImpostor(p))
                {
                    continue;
                }
                if (listFilter == ListFilter.FAMOUS_PLAYERS && !ConfigEntry.FAMOUS_PLAYERS.getList().contains(p.getName().toLowerCase()))
                {
                    continue;
                }
                if (listFilter == ListFilter.PLAYERS && plugin.sl.isVanished(p.getName()))
                {
                    continue;
                }

                final Displayable display = plugin.rm.getDisplay(p);
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

    private enum ListFilter
    {
        PLAYERS,
        STAFF,
        VANISHED_STAFF,
        TELNET_SESSIONS,
        FAMOUS_PLAYERS,
        IMPOSTORS
    }
}