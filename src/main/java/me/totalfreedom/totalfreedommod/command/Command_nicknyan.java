package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Essentials Interface Command - Nyanify your nickname.", usage = "/<command> <<nick> | off>")
public class Command_nicknyan extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if ("off".equals(args[0]))
        {
            plugin.esb.setNickname(sender.getName(), null);
            msg("Nickname cleared.");
            return true;
        }

        final String nickPlain = ChatColor.stripColor(FUtil.colorize(args[0].trim()));

        if (!nickPlain.matches("^[a-zA-Z_0-9" + ChatColor.COLOR_CHAR + "]+$"))
        {
            msg("That nickname contains invalid characters.");
            return true;
        }
        else if (nickPlain.length() < 4 || nickPlain.length() > 30)
        {
            msg("Your nickname must be between 4 and 30 characters long.");
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player == playerSender)
            {
                continue;
            }
            if (player.getName().equalsIgnoreCase(nickPlain) || ChatColor.stripColor(player.getDisplayName()).trim().equalsIgnoreCase(nickPlain))
            {
                msg("That nickname is already in use.");
                return true;
            }
        }

        final StringBuilder newNick = new StringBuilder();

        final char[] chars = nickPlain.toCharArray();
        for (char c : chars)
        {
            newNick.append(FUtil.randomChatColor()).append(c);
        }

        newNick.append(ChatColor.WHITE);

        plugin.esb.setNickname(sender.getName(), newNick.toString());

        msg("Your nickname is now: " + newNick.toString());

        return true;
    }
}
