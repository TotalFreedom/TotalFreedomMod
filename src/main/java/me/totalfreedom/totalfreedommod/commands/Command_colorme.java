package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import java.util.Iterator;
import java.util.Map;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Essentials Interface Command - Color your current nickname.", usage = "/<command> <color>")
public class Command_colorme extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if ("list".equalsIgnoreCase(args[0]))
        {
            playerMsg("Colors: " + StringUtils.join(FUtil.CHAT_COLOR_NAMES.keySet(), ", "));
            return true;
        }

        final String needle = args[0].trim().toLowerCase();
        ChatColor color = null;
        final Iterator<Map.Entry<String, ChatColor>> it = FUtil.CHAT_COLOR_NAMES.entrySet().iterator();
        while (it.hasNext())
        {
            final Map.Entry<String, ChatColor> entry = it.next();
            if (entry.getKey().contains(needle))
            {
                color = entry.getValue();
                break;
            }
        }

        if (color == null)
        {
            playerMsg("Invalid color: " + needle + " - Use \"/colorme list\" to list colors.");
            return true;
        }

        final String newNick = color + ChatColor.stripColor(sender_p.getDisplayName()).trim() + ChatColor.WHITE;

        plugin.esb.setNickname(sender.getName(), newNick);

        playerMsg("Your nickname is now: " + newNick);

        return true;
    }
}
