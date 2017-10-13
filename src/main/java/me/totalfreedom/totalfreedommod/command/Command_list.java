

package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Displayable;
import java.util.Iterator;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import me.totalfreedom.totalfreedommod.rank.Rank;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.BOTH)
@CommandParameters(description = "Lists the real names of all online players.", usage = "/<command> [-a | -i | -f]", aliases = "who")
public class Command_list extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length > 1) {
            return false;
        }
        if (FUtil.isFromHostConsole(sender.getName())) {
            final List<String> names = new ArrayList<String>();
            for (final Player player : this.server.getOnlinePlayers()) {
                names.add(player.getName());
            }
            this.msg("There are " + names.size() + "/" + this.server.getMaxPlayers() + " players online:\n" + StringUtils.join((Iterable)names, ", "), ChatColor.WHITE);
            return true;
        }
        ListFilter listFilter = null;
        if (args.length == 1) {
            final String s = args[0];
            switch (s) {
                case "-a": {
                    listFilter = ListFilter.ADMINS;
                    break;
                }
                case "-v": {
                    listFilter = ListFilter.VANISHED_ADMINS;
                    break;
                }
                case "-i": {
                    listFilter = ListFilter.IMPOSTORS;
                    break;
                }
                case "-f": {
                    listFilter = ListFilter.FAMOUS_PLAYERS;
                    break;
                }
                default: {
                    return false;
                }
            }
        }
        else {
            listFilter = ListFilter.PLAYERS;
        }
        if (listFilter == ListFilter.VANISHED_ADMINS && !((TotalFreedomMod)this.plugin).al.isAdmin((CommandSender)playerSender)) {
            this.msg("/list [-a | -i | -f ]", ChatColor.WHITE);
            return true;
        }
        final StringBuilder onlineStats = new StringBuilder();
        final StringBuilder onlineUsers = new StringBuilder();
        onlineStats.append(ChatColor.BLUE).append("There are ").append(ChatColor.RED).append(this.server.getOnlinePlayers().size() - Command_vanish.vanished.size());
        onlineStats.append(ChatColor.BLUE).append(" out of a maximum ").append(ChatColor.RED).append(this.server.getMaxPlayers());
        onlineStats.append(ChatColor.BLUE).append(" players online.");
        final List<String> names2 = new ArrayList<String>();
        for (final Player player2 : this.server.getOnlinePlayers()) {
            if (listFilter == ListFilter.ADMINS && !((TotalFreedomMod)this.plugin).al.isAdmin((CommandSender)player2)) {
                continue;
            }
            if (listFilter == ListFilter.ADMINS && Command_vanish.vanished.contains(player2)) {
                continue;
            }
            if (listFilter == ListFilter.VANISHED_ADMINS && !Command_vanish.vanished.contains(player2)) {
                continue;
            }
            if (listFilter == ListFilter.IMPOSTORS && !((TotalFreedomMod)this.plugin).al.isAdminImpostor(player2)) {
                continue;
            }
            if (listFilter == ListFilter.FAMOUS_PLAYERS && !ConfigEntry.FAMOUS_PLAYERS.getList().contains(player2.getName().toLowerCase())) {
                continue;
            }
            if (listFilter == ListFilter.PLAYERS && Command_vanish.vanished.contains(player2)) {
                continue;
            }
            final Displayable display = ((TotalFreedomMod)this.plugin).rm.getDisplay((CommandSender)player2);
            names2.add(display.getColoredTag() + player2.getName());
        }
        final String playerType = (listFilter == null) ? "players" : listFilter.toString().toLowerCase().replace('_', ' ');
        onlineUsers.append("Connected ");
        onlineUsers.append(playerType + ": ");
        onlineUsers.append(StringUtils.join((Iterable)names2, ChatColor.WHITE + ", "));
        if (senderIsConsole) {
            sender.sendMessage(ChatColor.stripColor(onlineStats.toString()));
            sender.sendMessage(ChatColor.stripColor(onlineUsers.toString()));
        }
        else {
            sender.sendMessage(onlineStats.toString());
            sender.sendMessage(onlineUsers.toString());
        }
        names2.clear();
        return true;
    }
    
    private enum ListFilter
    {
        PLAYERS, 
        ADMINS, 
        VANISHED_ADMINS, 
        FAMOUS_PLAYERS, 
        IMPOSTORS;
    }
}
