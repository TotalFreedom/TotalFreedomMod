

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
@CommandParameters(description = "Toggle PVP mode for players.", usage = "/<command> [[-s] <player> [reason] | list | purge | all]", aliases = "pvpblock,pvpmode,pvpman,pvman")
public class Command_blockpvp extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, String[] args, final boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }
        if (args[0].equals("list")) {
            this.msg("PVP is blocked for players:");
            int count = 0;
            for (final Player mp : this.server.getOnlinePlayers()) {
                final FPlayer info = ((TotalFreedomMod)this.plugin).pl.getPlayer(mp);
                if (info.isPVPBlock()) {
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
            FUtil.adminAction(sender.getName(), "Enabling PVP for all players.", true);
            int count = 0;
            for (final Player mp : this.server.getOnlinePlayers()) {
                final FPlayer info = ((TotalFreedomMod)this.plugin).pl.getPlayer(mp);
                if (info.isPVPBlock()) {
                    info.setPVPBlock(false);
                    ++count;
                }
            }
            this.msg("Enabled PVP for " + count + " players.");
            return true;
        }
        if (args[0].equals("all")) {
            FUtil.adminAction(sender.getName(), "Disabling PVP for all non-admins", true);
            int counter = 0;
            for (final Player player : this.server.getOnlinePlayers()) {
                if (!((TotalFreedomMod)this.plugin).al.isAdmin((CommandSender)player)) {
                    final FPlayer playerdata = ((TotalFreedomMod)this.plugin).pl.getPlayer(player);
                    playerdata.setPVPBlock(true);
                    ++counter;
                }
            }
            this.msg("Disabling PVP for " + counter + " players.");
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
        if (playerdata2.isPVPBlock()) {
            FUtil.adminAction(sender.getName(), "Enabling PVP for " + player2.getName(), true);
            playerdata2.setPVPBlock(false);
            this.msg("Enabling PVP  for  " + player2.getName());
            this.msg((CommandSender)player2, "Your PVP have been enabled.", ChatColor.GREEN);
        }
        else {
            if (((TotalFreedomMod)this.plugin).al.isAdmin((CommandSender)player2)) {
                this.msg(player2.getName() + " is an admin, and his PVP cannot be disabled.");
                return true;
            }
            FUtil.adminAction(sender.getName(), "Disabling PVP for " + player2.getName(), true);
            playerdata2.setPVPBlock(true);
            if (smite) {
                Command_smite.smite(player2, sender);
            }
            if (reason != null) {
                this.msg((CommandSender)player2, "Your PVP has been disabled. Reason: " + reason, ChatColor.RED);
            }
            else {
                this.msg((CommandSender)player2, "Your PVP has been disabled.", ChatColor.RED);
            }
            this.msg("Disabled PVP for " + player2.getName());
        }
        return true;
    }
}
