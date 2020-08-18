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
@CommandParameters(description = "Information on how to apply for staff.", usage = "/<command>", aliases = "si")
public class Command_staffinfo extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        List<String> staffInfo = ConfigEntry.STAFF_INFO.getStringList();

        if (staffInfo.isEmpty())
        {
            msg("The staff information section of the config.yml file has not been configured.", ChatColor.RED);
        }
        else
        {
            msg(FUtil.colorize(StringUtils.join(staffInfo, "\n")));
        }
        return true;
    }
}
