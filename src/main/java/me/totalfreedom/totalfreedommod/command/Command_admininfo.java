package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Information on how to apply for admin.", usage = "/<command>", aliases = "ai")
public class Command_admininfo extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        List<String> adminInfo = ConfigEntry.ADMININFO.getStringList();

        if (adminInfo.isEmpty())
        {
            msg("There is no admin information set in the config.", ChatColor.RED);
        }
        else
        {
            msg(FUtil.colorize(StringUtils.join(adminInfo, "\n")));
        }

        return true;
    }
}
