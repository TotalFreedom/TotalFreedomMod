package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows all banned player names. Superadmins may optionally use 'purge' to clear the list.", usage = "/<command> [purge]")
public class Command_tfbanlist extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("purge"))
            {
                if (senderIsConsole || plugin.al.isAdmin(sender))
                {
                    try
                    {
                        FUtil.adminAction(sender.getName(), "Purging the ban list", true);
                        plugin.bm.purgeNameBans();
                        sender.sendMessage(ChatColor.GRAY + "Ban list has been purged.");
                    }
                    catch (Exception ex)
                    {
                        FLog.severe(ex);
                    }

                    return true;
                }
                else
                {
                    playerMsg("You do not have permission to purge the ban list, you may only view it.");
                }
            }
        }

        playerMsg(plugin.bm.getUsernameBans().size() + " name bans total");

        return true;
    }
}
