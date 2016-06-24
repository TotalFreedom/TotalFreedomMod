package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Broadcasts the given message as the console, includes sender name.", usage = "/<command> <message>")
public class Command_say extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        String message = StringUtils.join(args, " ");

        if (senderIsConsole && FUtil.isFromHostConsole(sender.getName()))
        {
            if (message.equalsIgnoreCase("WARNING: Server is restarting, you will be kicked"))
            {
                FUtil.bcastMsg("Server is going offline.", ChatColor.GRAY);

                for (Player player : server.getOnlinePlayers())
                {
                    player.kickPlayer("Server is going offline, come back in about 20 seconds.");
                }

                server.shutdown();

                return true;
            }
        }

        FUtil.bcastMsg(String.format("[Server:%s] %s", sender.getName(), message), ChatColor.LIGHT_PURPLE);

        return true;
    }
}
