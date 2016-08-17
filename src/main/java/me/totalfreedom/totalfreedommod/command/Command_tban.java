package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily bans a player for five minutes.", usage = "/<command> <player> [reason]", aliases = "noob")
public class Command_tban extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }
        final Player player = this.getPlayer(args[0]);
        if (player == null) {
            this.msg(FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
            return true;
        }
        String reason;
        if (args.length > 1) {
            reason = StringUtils.join((Object[])args, " ", 1, args.length);
        }
        else {
            reason = "You have been temporarily banned for 5 minutes.";
        }
        final Location targetPos = player.getLocation();
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                final Location strike_pos = new Location(targetPos.getWorld(), (double)(targetPos.getBlockX() + x), (double)targetPos.getBlockY(), (double)(targetPos.getBlockZ() + z));
                targetPos.getWorld().strikeLightning(strike_pos);
            }
        }
        FUtil.adminAction(sender.getName(), "Tempbanning: " + player.getName() + " for 5 minutes.", true);
        if (reason != null && reason != "You have been temporarily banned for 5 minutes.") {
        FUtil.bcastMsg("Reason: " + reason, ChatColor.RED);
        }        ((TotalFreedomMod)this.plugin).bm.addBan(Ban.forPlayer(player, sender, FUtil.parseDateOffset("5m"), reason));
        player.kickPlayer(ChatColor.RED + "You have been temporarily banned for five minutes. Please read totalfreedom.me for more info.");
        return true;
    }
}
