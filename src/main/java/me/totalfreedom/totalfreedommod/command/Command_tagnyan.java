package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gives you a tag with random colors", usage = "/<command> <tag>", aliases = "tn")
public class Command_tagnyan extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        final StringBuilder tag = new StringBuilder();

        for (char c : ChatColor.stripColor(FUtil.colorize(StringUtils.join(args, " "))).toCharArray())
        {
            tag.append(FUtil.randomChatColor()).append(c);
        }

        String tagStr = tag.toString();
        for (String word : Command_tag.FORBIDDEN_WORDS)
        {
            if (tagStr.contains(word))
            {
                msg("That tag contains a forbidden word.");
                return true;
            }
        }

        final FPlayer data = plugin.pl.getPlayer(playerSender);
        data.setTag(tagStr);

        msg("Set tag to " + tag);

        return true;
    }
}
