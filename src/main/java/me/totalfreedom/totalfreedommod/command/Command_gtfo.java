package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.apache.commons.lang3.*;
import me.totalfreedom.totalfreedommod.util.*;
import me.totalfreedom.totalfreedommod.*;
import org.bukkit.*;
import net.pravian.aero.util.*;
import me.totalfreedom.totalfreedommod.banning.*;
import java.util.*;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Makes someone GTFO (deop and ip ban by username).", usage = "/<command> <partialname> <reason>")
public class Command_gtfo extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }
        final Player player = this.getPlayer(args[0]);
        if (player == null) {
            this.msg(FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
            return true;
        }
        String reason = null;
        if (args.length >= 2) {
            reason = org.apache.commons.lang3.StringUtils.join(ArrayUtils.subarray((Object[])args, 1, args.length), " ");
        }
        FUtil.bcastMsg(player.getName() + " has been a VERY naughty, naughty boy.", ChatColor.RED);
        try {
            ((TotalFreedomMod)this.plugin).web.undo(player, 25);
        }
        catch (NoClassDefFoundError noClassDefFoundError) {}
        ((TotalFreedomMod)this.plugin).rb.rollback(player.getName());
        player.setOp(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        final Location targetPos = player.getLocation();
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                final Location strike_pos = new Location(targetPos.getWorld(), (double)(targetPos.getBlockX() + x), (double)targetPos.getBlockY(), (double)(targetPos.getBlockZ() + z));
                targetPos.getWorld().strikeLightning(strike_pos);
            }
        }
        final String ip = FUtil.getFuzzyIp(Ips.getIp(player));
        final StringBuilder bcast = new StringBuilder().append(ChatColor.RED).append("Banning: ").append(player.getName()).append(", IP: ").append(ip);
        if (reason != null) {
            bcast.append(" - Reason: ").append(ChatColor.YELLOW).append(reason);
        }
        FUtil.bcastMsg(bcast.toString());
        ((TotalFreedomMod)this.plugin).bm.addBan(Ban.forPlayerFuzzy(player, sender, null, reason));
        player.kickPlayer(ChatColor.RED + "GTFO");
        return true;
    }
}
