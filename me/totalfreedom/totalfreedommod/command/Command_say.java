package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.apache.commons.lang3.*;
import me.totalfreedom.totalfreedommod.util.*;
import org.bukkit.*;
import java.util.*;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Broadcasts the given message as the console, includes sender name.", usage = "/<command> <message>")
public class Command_say extends FreedomCommand
{
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }
        final String message = StringUtils.join((Object[])args, " ");
        if (senderIsConsole && FUtil.isFromHostConsole(sender.getName()) && message.equalsIgnoreCase("WARNING: Server is restarting, you will be kicked")) {
            FUtil.bcastMsg("Server is going offline.", ChatColor.GRAY);
            for (final Player player : this.server.getOnlinePlayers()) {
                player.kickPlayer("Server is going offline, come back in about 20 seconds.");
            }
            this.server.shutdown();
            return true;
        }
        FUtil.bcastMsg(String.format("[Server:%s] %s", sender.getName(), message), ChatColor.LIGHT_PURPLE);
        return true;
    }
}
