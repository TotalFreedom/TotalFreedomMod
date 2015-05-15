package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Quick Op - op someone based on a partial name.", usage = "/<command> <partialname>")
public class Command_qop extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
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

        final List<String> matchedPlayerNames = new ArrayList<String>();
        for (final Player player : server.getOnlinePlayers())
        {
            if (player.getName().toLowerCase().contains(targetName) || player.getDisplayName().toLowerCase().contains(targetName))
            {
                if (!player.isOp())
                {
                    matchedPlayerNames.add(player.getName());
                    player.setOp(true);
                    player.sendMessage(TFM_Command.YOU_ARE_OP);
                }
            }
        }

        if (!matchedPlayerNames.isEmpty())
        {
            if (!silent)
            {
                TFM_Util.adminAction(sender.getName(), "Opping " + StringUtils.join(matchedPlayerNames, ", "), false);
            }
        }
        else
        {
            playerMsg("No targets matched.");
        }

        return true;
    }
}
