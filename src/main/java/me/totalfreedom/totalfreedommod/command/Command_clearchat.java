package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Clears the chat.", usage = "/<command>", aliases = "cc")
public class Command_clearchat extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (!plugin.sl.isStaff(player))
            {
                for (int i = 0; i < 100; i++)
                {
                    player.sendMessage("");
                }
            }
        }
        FUtil.staffAction(sender.getName(), "Cleared chat", true);
        return true;
    }
}
