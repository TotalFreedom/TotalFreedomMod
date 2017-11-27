package me.totalfreedom.totalfreedommod.command;

import java.util.Iterator;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gives you a rainbow tag", usage = "/<command> <tag>")
public class Command_rainbowtag extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }
      
        final String tag = ChatColor.stripColor(FUtil.colorize(StringUtils.join(args, " ")));
        
        if(tag.length() > 20)
        {
            msg("That tag is too long (Max is 20 characters).");
            return true;
        }
        
        for (String word : Command_tag.FORBIDDEN_WORDS) 
        {
            if (tag.contains(word))
            {
                msg("That tag contains a forbidden word.");
                return true;
            }
        }

        plugin.pl.getPlayer(playerSender).setTag(FUtil.rainbowify(tag));
  
        msg("Set tag to " + tag);

        return true;
    }
}