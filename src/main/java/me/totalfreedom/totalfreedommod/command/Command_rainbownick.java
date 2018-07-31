package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Essentials Interface Command - Rainbowify your nickname.", usage = "/<command> <nick>")
public class Command_rainbownick extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        final String nickPlain = ChatColor.stripColor(FUtil.colorize(args[0].trim()));

        if (!nickPlain.matches("^[a-zA-Z_0-9" + ChatColor.COLOR_CHAR + "]+$"))
        {
            msg("That nickname contains invalid characters.");
            return true;
        }

        if (nickPlain.length() < 4 || nickPlain.length() > 30)
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

        final String newNick = FUtil.rainbowify(ChatColor.stripColor(FUtil.colorize(nickPlain)));

        plugin.esb.setNickname(sender.getName(), newNick);

        msg("Your nickname is now: " + newNick);

        return true;
    }
}