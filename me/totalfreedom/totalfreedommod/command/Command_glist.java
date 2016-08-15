package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import me.totalfreedom.totalfreedommod.*;
import org.apache.commons.lang3.*;
import me.totalfreedom.totalfreedommod.util.*;
import me.totalfreedom.totalfreedommod.banning.*;
import me.totalfreedom.totalfreedommod.player.*;
import java.util.*;
import org.bukkit.ChatColor;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Bans or unbans any player, even those who are not logged in anymore.", usage = "/<command> <purge | ban <username> [reason] | unban <username>>")
public class Command_glist extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }
        if (args.length == 1) {
            if ("purge".equals(args[0])) {
                this.checkRank(Rank.SENIOR_ADMIN);
                ((TotalFreedomMod)this.plugin).pl.purgeAllData();
                this.msg("Purged playerbase.");
                return true;
            }
            return false;
        }
        else {
            if (args.length < 2) {
                return false;
            }
            final List<String> ips = new ArrayList<String>();
            final Player player = this.getPlayer(args[1]);
            String username;
            if (player == null) {
                final PlayerData entry = ((TotalFreedomMod)this.plugin).pl.getData(args[1]);
                if (entry == null) {
                    this.msg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                    return true;
                }
                username = entry.getUsername();
                ips.addAll(entry.getIps());
            }
            else {
                final PlayerData entry = ((TotalFreedomMod)this.plugin).pl.getData(player);
                username = player.getName();
                ips.addAll(entry.getIps());
            }
            if ("ban".equals(args[0])) { 
                FUtil.adminAction(sender.getName(), "Banning " + username, true); //Globally send username
                sender.sendMessage(ChatColor.RED + "IPs: " + StringUtils.join((Iterable)ips, ", ")); //Only send the IPs to the sender.
                final String reason = (args.length > 2) ? StringUtils.join((Object[])args, " ", 2, args.length) : null;
                if (reason != null) {
                FUtil.bcastMsg("Reason: " + reason, ChatColor.RED);
                }
                final Ban ban = Ban.forPlayerName(username, sender, null, reason);
                for (final String ip : ips) {
                    ban.addIp(ip);
                    ban.addIp(FUtil.getFuzzyIp(ip));
                }
                ((TotalFreedomMod)this.plugin).bm.addBan(ban);
                if (player != null) {
                    player.kickPlayer(ban.bakeKickMessage());
                }
                return true;
            }
            if ("unban".equals(args[0])) {
                FUtil.adminAction(sender.getName(), "Unbanning " + username, true); //Globally send username
                sender.sendMessage(ChatColor.RED + "IPs: " + StringUtils.join((Iterable)ips, ", ")); //Only send the IPs to the sender.
                ((TotalFreedomMod)this.plugin).bm.removeBan(((TotalFreedomMod)this.plugin).bm.getByUsername(username));
                for (final String ip2 : ips) {
                    Ban ban2 = ((TotalFreedomMod)this.plugin).bm.getByIp(ip2);
                    if (ban2 != null) {
                        ((TotalFreedomMod)this.plugin).bm.removeBan(ban2);
                    }
                    ban2 = ((TotalFreedomMod)this.plugin).bm.getByIp(FUtil.getFuzzyIp(ip2));
                    if (ban2 != null) {
                        ((TotalFreedomMod)this.plugin).bm.removeBan(ban2);
                    }
                }
                return true;
            }
            return false;
        }
    }
}
