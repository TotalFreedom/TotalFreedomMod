package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Change your default chat color.", usage = "/<command> <color>")
public class Command_chatcolor extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        VPlayer vPlayer = plugin.pv.getVerificationPlayer(playerSender);

        if (args[0].equals("clear"))
        {
            vPlayer.setColor(null);
            msg("Default chat color cleared.");
            return true;
        }

        if (args[0].equalsIgnoreCase("k")
            || args[0].equalsIgnoreCase("0")
            || args[0].equalsIgnoreCase("m"))
        {
            msg("You are not allowed to use that color as default.");
            return true;
        }

        ChatColor color = ChatColor.getByChar(args[0]);
        if (color == null)
        {
            msg("Please enter a valid color. Example: a, 2, e");
            return true;
        }

        vPlayer.setColor(color);
        msg("Default chat color set to \"" + args[0] + ".\"");
        return true;
    }
}
