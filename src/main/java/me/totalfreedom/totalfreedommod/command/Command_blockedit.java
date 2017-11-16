

package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import me.totalfreedom.totalfreedommod.rank.Rank;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Blocks all block placing for player with brute force.", usage = "/<command> [[-s] <player> [reason] | list | purge | all]", aliases = "editmute")
public class Command_blockedit extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, String[] args, final boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }
        if (args[0].equals("list")) {
            this.msg("Blocked block edits players:");
            int count = 0;
            for (final Player mp : this.server.getOnlinePlayers()) {
                final FPlayer info = ((TotalFreedomMod)this.plugin).pl.getPlayer(mp);
                if (info.isEditBlock()) {
                    this.msg("- " + mp.getName());
                    ++count;
                }
            }
            if (count == 0) {
                this.msg("- none");
            }
            return true;
        }
        if (args[0].equals("purge")) {
            FUtil.adminAction(sender.getName(), "Unblocking block edits for all players.", true);
            int count = 0;
            for (final Player mp : this.server.getOnlinePlayers()) {
                final FPlayer info = ((TotalFreedomMod)this.plugin).pl.getPlayer(mp);
                if (info.isEditBlock()) {
                    info.setEditBlocked(false);
                    ++count;
                }
            }
            this.msg("Unblocked all block edit for " + count + " players.");
            return true;
        }
        if (args[0].equals("all")) {
            FUtil.adminAction(sender.getName(), "Blocking block edits for all non-Superadmins", true);
            int counter = 0;
            for (final Player player : this.server.getOnlinePlayers()) {
                if (!((TotalFreedomMod)this.plugin).al.isAdmin((CommandSender)player)) {
                    final FPlayer playerdata = ((TotalFreedomMod)this.plugin).pl.getPlayer(player);
                    playerdata.setEditBlocked(true);
                    ++counter;
                }
            }
            this.msg("Blocked all block edit for " + counter + " players.");
            return true;
        }
        final boolean smite = args[0].equals("-s");
        if (smite) {
            args = (String[])ArrayUtils.subarray((Object[])args, 1, args.length);
            if (args.length < 1) {
                return false;
            }
        }
        final Player player2 = this.getPlayer(args[0]);
        if (player2 == null) {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }
        String reason = null;
        if (args.length > 1) {
            reason = StringUtils.join((Object[])args, " ", 1, args.length);
        }
        final FPlayer playerdata2 = ((TotalFreedomMod)this.plugin).pl.getPlayer(player2);
        if (playerdata2.isEditBlock()) {
            FUtil.adminAction(sender.getName(), "Unblocking all block edits for " + player2.getName(), true);
            playerdata2.setEditBlocked(false);
            this.msg("Unblocking all block edits for " + player2.getName());
            this.msg((CommandSender)player2, "You block edits have been unblocked.", ChatColor.RED);
        }
        else {
            if (((TotalFreedomMod)this.plugin).al.isAdmin((CommandSender)player2)) {
                this.msg(player2.getName() + " is a superadmin, and his block edits can't be blocked .");
                return true;
            }
            FUtil.adminAction(sender.getName(), "Blocking block edits for " + player2.getName(), true);
            playerdata2.setEditBlocked(true);
            if (smite) {
                Command_smite.smite(player2, sender);
            }
            if (reason != null) {
                this.msg((CommandSender)player2, "You block edits have been blocked. Reason: " + reason, ChatColor.RED);
            }
            else {
                this.msg((CommandSender)player2, "You block edits have been blocked.", ChatColor.RED);
            }
            this.msg("Blocked all block edits for " + player2.getName());
        }
        return true;
    }
}
