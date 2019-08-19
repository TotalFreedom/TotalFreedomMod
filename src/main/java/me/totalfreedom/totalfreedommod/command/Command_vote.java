package me.totalfreedom.totalfreedommod.command;

import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Information on how to vote", usage = "/<command>")
public class Command_vote extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        List<String> voteInfo = ConfigEntry.VOTING_INFO.getStringList();

        if (voteInfo.isEmpty())
        {
            msg("There is no voting information set in the config.", ChatColor.RED);
        }
        else
        {
            msg(FUtil.colorize(StringUtils.join(voteInfo, "\n")));
        }

        return true;
    }
}
