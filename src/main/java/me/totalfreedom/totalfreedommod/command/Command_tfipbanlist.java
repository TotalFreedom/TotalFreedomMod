package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows all banned IPs. Superadmins may optionally use 'purge' to clear the list.", usage = "/<command> [purge]")
public class Command_tfipbanlist extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("purge"))
            {
                if (senderIsConsole || plugin.al.isAdmin(sender))
                {
                    try
                    {
                        plugin.bm.purgeIpBans();
                        FUtil.adminAction(sender.getName(), "Purging the IP ban list", true);

                        sender.sendMessage(ChatColor.GRAY + "IP ban list has been purged.");
                    }
                    catch (Exception ex)
                    {
                        FLog.severe(ex);
                    }

                    return true;
                }
                else
                {
                    msg("You do not have permission to purge the IP ban list, you may only view it.");
                }
            }
        }

        msg(plugin.bm.getIpBans() + " IP bans total");

        return true;
    }
}
