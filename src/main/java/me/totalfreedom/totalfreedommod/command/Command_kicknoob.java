package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Kick all non-superadmins on server.", usage = "/<command>")
public class Command_kicknoob extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.adminAction(sender.getName(), "Disconnecting all non-superadmins.", true);

        for (Player player : server.getOnlinePlayers())
        {
            if (!plugin.al.isAdmin(player))
            {
                player.kickPlayer(ChatColor.RED + "All non-superadmins were kicked by " + sender.getName() + ".");
            }
        }

        return true;
    }
}
