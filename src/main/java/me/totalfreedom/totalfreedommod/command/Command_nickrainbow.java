package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gives you a rainbow nickname", usage = "/<command> <tag>")
public class Command_nickrainbow extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        final String nick = ChatColor.stripColor(FUtil.colorize(StringUtils.join(args, " ")));

        if (!plugin.al.isAdmin(sender))
        {
            final String rawNick = ChatColor.stripColor(nick).toLowerCase();

            if (rawNick.length() > 20)
            {
                msg("That nick is too long (Max is 20 characters).");
                return true;
            }

            for (String word : Command_tag.FORBIDDEN_WORDS)
            {
                if (rawNick.contains(word))
                {
                    msg("That nick contains a forbidden word.");
                    return true;
                }
            }
        }

        plugin.pl.getPlayer(playerSender).setTag(FUtil.rainbowify(nick));

        msg("Set nick to " + nick);

        return true;
    }
}