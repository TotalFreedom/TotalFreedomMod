package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Kicks everyone and stops the server.", usage = "/<command>")
public class Command_stop extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.bcastMsg("Server is going offline!", ChatColor.LIGHT_PURPLE);

        for (Player player : server.getOnlinePlayers())
        {
            player.kickPlayer("Server is going offline, come back in about 20 seconds.");
        }

        server.shutdown();

        return true;
    }
}
