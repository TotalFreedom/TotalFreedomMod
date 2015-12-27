package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Telnet command - Send a chat message with chat formatting over telnet.", usage = "/<command> <message...>")
public class Command_csay extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0)
        {
            FUtil.bcastMsg(String.format("§7[CONSOLE]§f<§c%s§f> %s", sender.getName(), StringUtils.join(args, " ")));
        }
        return true;
    }
}
