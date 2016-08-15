package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.apache.commons.lang3.*;
import me.totalfreedom.totalfreedommod.util.*;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Telnet command - Send a chat message with chat formatting over telnet.", usage = "/<command> <message...>", aliases = "csay")
public class Command_consolesay extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length > 0) {
            FUtil.bcastMsg(String.format("§7[CONSOLE]§f<§c%s§f> %s", sender.getName(), StringUtils.join((Object[])args, " ")));
        }
        return true;
    }
}
