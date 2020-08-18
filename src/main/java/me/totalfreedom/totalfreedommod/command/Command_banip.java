package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Bans the specified ip.", usage = "/<command> <ip> [reason] [-q]")
public class Command_banip extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        boolean silent = false;

        String reason = null;
        
        String ip = args[0];

        if (!FUtil.isValidIPv4(ip))
        {
            msg(ip + " is not a valid IP address", ChatColor.RED);
            return true;
        }

        if (plugin.bm.getByIp(ip) != null)
        {
            msg("The IP " + ip + " is already banned", ChatColor.RED);
            return true;
        }

        if (args[args.length - 1].equalsIgnoreCase("-q"))
        {
            silent = true;

            if (args.length >= 2)
            {
                reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length - 1), " ");
            }
        }
        else if (args.length > 1)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        }

        // Ban player
        Ban ban = Ban.forPlayerIp(ip, sender, null, reason);
        plugin.bm.addBan(ban);

        // Kick player and handle others on IP
        for (Player player : server.getOnlinePlayers())
        {
            if (FUtil.getIp(player).equals(ip))
            {
                player.kickPlayer(ban.bakeKickMessage());
            }

            if (!silent)
            {
                // Broadcast
                FLog.info(ChatColor.RED + sender.getName() + " - Banned the IP " + ip);
                String message = ChatColor.RED + sender.getName() + " - Banned " + (plugin.sl.isStaff(player) ? "the IP " + ip : "an IP");
                player.sendMessage(message);
            }
        }

        return true;
    }
}