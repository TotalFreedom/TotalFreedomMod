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
@CommandParameters(description = "Sets your prefix with rainbow coloring", usage = "/<command> <tag>", aliases = "tn")
public class Command_rainbowtag extends FreedomCommand
{

    public static final List<String> FORBIDDEN_WORDS = Arrays.asList(new String[]
    {
        "admin", "owner", "moderator", "developer", "console", "sra", "sta", "sa", "Super Admin", "Telnet Admin", "Telnet Clan Admin", "Senior Admin"
    });

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
            tag.append(FUtil.rainbowChatColor()).append(c);
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
        
        if (tag.length > 20)
        {
            msg("That tag is too long (Max is 20 characters).");
            return true;
        }
        
        for (String word : FORBIDDEN_WORDS)
        {
            if (tag.contains(word))
            {
                msg("That tag contains a forbidden word.");
                return true;
            }
        }

        final FPlayer data = plugin.pl.getPlayer(playerSender);
        data.setTag(tagStr);

        msg("Tag set to " + tag);

        return true;
    }
}
