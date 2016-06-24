package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows all banned player names. Superadmins may optionally use 'purge' to clear the list.", usage = "/<command> [purge]")
public class Command_banlist extends FreedomCommand {

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("purge")) {
                checkRank(Rank.SENIOR_ADMIN);

                FUtil.adminAction(sender.getName(), "Purging the ban list", true);
                int amount = plugin.bm.purge();
                sender.sendMessage(ChatColor.GRAY + "Purged " + amount + " player bans.");

                return true;

            }

            return false;
        }

        msg(plugin.bm.getAllBans().size() + " player bans ("
                + plugin.bm.getUsernameBans().size() + " usernames, "
                + plugin.bm.getIpBans().size() + " IPs)");

        return true;
    }
}
