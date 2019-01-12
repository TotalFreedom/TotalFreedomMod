package me.totalfreedom.totalfreedommod.command;

import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Information on how to apply for Master Builder.", usage = "/<command>", aliases = "mbi")
public class Command_masterbuilderinfo extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        List<String> masterBuilderInfo = ConfigEntry.MASTER_BUILDER_INFO.getStringList();

        if (masterBuilderInfo.isEmpty())
        {
            msg("There is no Master Builder information set in the config.", ChatColor.RED);
        }
        else
        {
            msg(FUtil.colorize(StringUtils.join(masterBuilderInfo, "\n")));
        }

        return true;
    }
}
