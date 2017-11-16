package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import java.util.Iterator;
import java.util.List;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.ChatColor;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import me.totalfreedom.totalfreedommod.rank.Rank;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gives you a tag with Rainbow", usage = "/<command> <tag>", aliases = "tn")
public class Command_rainbowtag extends FreedomCommand
{

    public static final List<String> FORBIDDEN_WORDS = Arrays.asList(new String[]
    {
        "admin", "owner", "moderator", "developer", "console", "SRA", "TCA", "SA"
    });

    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }
        final StringBuilder tag = new StringBuilder();
        for (final char c : ChatColor.stripColor(FUtil.colorize(StringUtils.join((Object[]) args, " "))).toCharArray())
        {
            tag.append(FUtil.rainbowChatColor()).append(c);
        }
        final String tagStr = tag.toString();
        for (final String word : FORBIDDEN_WORDS)
        {
            if (tagStr.contains(word))
            {
                this.msg("That tag contains a forbidden word.");
                return true;
            }
        }
        final FPlayer data = ((TotalFreedomMod) this.plugin).pl.getPlayer(playerSender);
        data.setTag(tagStr);
        this.msg("Set tag to " + (Object) tag);
        return true;
    }
}
