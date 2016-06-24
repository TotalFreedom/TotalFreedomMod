package me.totalfreedom.totalfreedommod.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Essentials Interface Command - Remove distracting things from nicknames of all players on server.", usage = "/<command>", aliases = "nc")
public class Command_nickclean extends FreedomCommand
{

    private static final ChatColor[] BLOCKED = new ChatColor[]
    {
        ChatColor.MAGIC,
        ChatColor.STRIKETHROUGH,
        ChatColor.ITALIC,
        ChatColor.UNDERLINE,
        ChatColor.BLACK
    };
    private static final Pattern REGEX = Pattern.compile(ChatColor.COLOR_CHAR + "[" + StringUtils.join(BLOCKED, "") + "]", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.adminAction(sender.getName(), "Cleaning all nicknames", false);

        for (final Player player : server.getOnlinePlayers())
        {
            final String playerName = player.getName();
            final String nickName = plugin.esb.getNickname(playerName);
            if (nickName != null && !nickName.isEmpty() && !nickName.equalsIgnoreCase(playerName))
            {
                final Matcher matcher = REGEX.matcher(nickName);
                if (matcher.find())
                {
                    final String newNickName = matcher.replaceAll("");
                    msg(ChatColor.RESET + playerName + ": \"" + nickName + ChatColor.RESET + "\" -> \"" + newNickName + ChatColor.RESET + "\".");
                    plugin.esb.setNickname(playerName, newNickName);
                }
            }
        }

        return true;
    }
}
