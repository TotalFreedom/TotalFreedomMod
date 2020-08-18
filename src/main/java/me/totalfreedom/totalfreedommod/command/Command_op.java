package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH, cooldown = 5)
@CommandParameters(description = "OP a player", usage = "/<command> <partialname>")
public class Command_op extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        boolean silent = false;
        if (args.length == 2)
        {
            silent = args[1].equalsIgnoreCase("-s");
        }

        final String targetName = args[0].toLowerCase();

        List<String> matchedPlayerNames = new ArrayList<>();
        for (final Player player : server.getOnlinePlayers())
        {
            if (player.getName().toLowerCase().contains(targetName) || player.getDisplayName().toLowerCase().contains(targetName)
                    || player.getName().contains(targetName) || player.getDisplayName().contains(targetName))
            {
                if (!player.isOp() && !plugin.sl.isVanished(player.getName()))
                {
                    matchedPlayerNames.add(player.getName());
                    player.setOp(true);
                    player.sendMessage(FreedomCommand.YOU_ARE_OP);
                }
            }
        }

        if (!matchedPlayerNames.isEmpty())
        {
            if (!silent)
            {
                FUtil.staffAction(sender.getName(), "Opping " + StringUtils.join(matchedPlayerNames, ", "), false);
            }
        }
        else
        {
            msg("Either the player is already opped, or the player could not be found.");
        }

        return true;
    }
}