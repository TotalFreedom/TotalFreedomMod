package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "This is a for the OP's that want to clear there chat", usage = "/<command>", aliases = "cmc")
public class Command_clearmychat extends F_Command
{
    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        for (int i = 0; i <= 150; i++)
        {
            msg("");
        }
        msg("You have cleared your own chat!", ChatColor.RED);
        return true;
    }
}
