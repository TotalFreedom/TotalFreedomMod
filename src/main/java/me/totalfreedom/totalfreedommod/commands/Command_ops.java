package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manager operators", usage = "/<command> <count | purge>")
public class Command_ops extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equals("count"))
        {
            int totalOps = server.getOperators().size();
            int onlineOps = 0;

            for (Player player : server.getOnlinePlayers())
            {
                if (player.isOp())
                {
                    onlineOps++;
                }
            }

            playerMsg("Online OPs: " + onlineOps);
            playerMsg("Offline OPs: " + (totalOps - onlineOps));
            playerMsg("Total OPs: " + totalOps);

            return true;
        }

        if (args[0].equals("purge"))
        {
            if (!plugin.al.isAdmin(sender))
            {
                noPerms();
                return true;
            }

            FUtil.adminAction(sender.getName(), "Purging all operators", true);

            for (OfflinePlayer player : server.getOperators())
            {
                player.setOp(false);
                if (player.isOnline())
                {
                    playerMsg(player.getPlayer(), FreedomCommand.YOU_ARE_NOT_OP);
                }
            }
            return true;
        }

        return false;
    }
}
