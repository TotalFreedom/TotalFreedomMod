package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Unbans the specified player.", usage = "/<command> <username> [-r]")
public class Command_unban extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0)
        {
            String username;
            final List<String> ips = new ArrayList<>();
            final PlayerData entry = plugin.pl.getData(args[0]);

            if (entry == null)
            {
                msg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                return true;
            }

            username = entry.getName();
            ips.addAll(entry.getIps());

            FUtil.staffAction(sender.getName(), "Unbanning " + username, true);
            msg(username + " has been unbanned along with the following IPs: " + StringUtils.join(ips, ", "));
            plugin.bm.removeBan(plugin.bm.getByUsername(username));

            if (args.length >= 2)
            {
                if (args[1].equalsIgnoreCase("-r"))
                {
                    plugin.cpb.restore(username);
                    msg("Restored edits for: " + username);
                }
            }

            for (String ip : ips)
            {
                Ban ban = plugin.bm.getByIp(ip);
                if (ban != null)
                {
                    plugin.bm.removeBan(ban);
                }
                ban = plugin.bm.getByIp(FUtil.getFuzzyIp(ip));
                if (ban != null)
                {
                    plugin.bm.removeBan(ban);
                }
            }
            return true;
        }
        return false;
    }
}
