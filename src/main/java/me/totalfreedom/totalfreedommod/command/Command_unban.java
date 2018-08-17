package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Unbans a player", usage = "/<command> <username> [-r[estore]]")
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
            final Player player = getPlayer(args[0]);

            if (entry == null)
            {
                msg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                return true;
            }

            username = entry.getUsername();
            ips.addAll(entry.getIps());

            FUtil.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
            plugin.bm.removeBan(plugin.bm.getByUsername(username));

            if (args.length >= 2)
            {
                if (args[1].equalsIgnoreCase("-r") || args[1].equalsIgnoreCase("-restore"))
                {
                    if (!plugin.cpb.isEnabled())
                    {
                        // Redo WorldEdits
                        try
                        {
                            plugin.web.redo(player, 15);
                        }
                        catch (NoClassDefFoundError | NullPointerException ex)
                        {
                        }
                        // Rollback
                        plugin.rb.undoRollback(username);
                    }
                    else
                    {
                        plugin.cpb.restore(username);
                    }
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
