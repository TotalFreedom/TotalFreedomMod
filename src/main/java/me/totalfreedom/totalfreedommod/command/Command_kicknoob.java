package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "Kick all non-staff on server.", usage = "/<command>", aliases = "kickall")
public class Command_kicknoob extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.staffAction(sender.getName(), "Disconnecting all non-staff.", true);

        for (Player player : server.getOnlinePlayers())
        {
            if (!plugin.sl.isStaff(player))
            {
                player.kickPlayer(ChatColor.RED + "All non-staff were kicked by " + sender.getName() + ".");
            }
        }

        return true;
    }
}
