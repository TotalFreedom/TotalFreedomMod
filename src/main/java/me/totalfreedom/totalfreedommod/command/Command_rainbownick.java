package me.totalfreedom.totalfreedommod.command;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import me.totalfreedom.totalfreedommod.rank.Rank;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Essentials Interface Command - Rainbowify your nickname.", usage = "/<command> <<nick> | off>")
public class Command_rainbownick extends FreedomCommand
{

    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }
        if ("off".equals(args[0]))
        {
            ((TotalFreedomMod) this.plugin).esb.setNickname(sender.getName(), null);
            this.msg("Nickname cleared.");
            return true;
        }

        final String nickPlain = ChatColor.stripColor(FUtil.colorize(args[0].trim()));

        if (!nickPlain.matches("^[a-zA-Z_0-9" + ChatColor.COLOR_CHAR + "]+$"))
        {
            msg("That nickname contains invalid characters.");
            return true;
        }

        if (nickPlain.length() < 4 || nickPlain.length() > 30)
        {
            this.msg("Your nickname must be between 4 and 30 characters long.");
            return true;
        }
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            if (player == playerSender)
            {
                continue;
            }
            if (player.getName().equalsIgnoreCase(nickPlain) || ChatColor.stripColor(player.getDisplayName()).trim().equalsIgnoreCase(nickPlain))
            {
                this.msg("That nickname is already in use.");
                return true;
            }
        }
        final StringBuilder newNick = new StringBuilder();
        final char[] charArray;
        final char[] chars = charArray = nickPlain.toCharArray();
        for (final char c : charArray)
        {
            newNick.append(FUtil.rainbowChatColor()).append(c);
        }
        newNick.append(ChatColor.WHITE);
        ((TotalFreedomMod) this.plugin).esb.setNickname(sender.getName(), newNick.toString());
        this.msg("Your nickname is now: " + newNick.toString());
        return true;
    }
}
