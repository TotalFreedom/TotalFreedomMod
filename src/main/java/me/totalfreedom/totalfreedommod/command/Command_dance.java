package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH, cooldown = 20)
@CommandParameters(description = "Dance with one or more people!", usage = "/<command> [player] [player2]")
public class Command_dance extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (args.length > 2)
        {
            return false;
        }
        if (args.length == 2)
        {
            Player player = Bukkit.getPlayer(args[0]);
            Player player2 = Bukkit.getPlayer(args[1]);
            if (player == null || player2 == null)
            {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }
            FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + ", "
                    + ChatColor.RED + player.getName() + ChatColor.AQUA + " and "
                    + ChatColor.RED + player2.getName() + ChatColor.AQUA + " dance together as a trio!");
            return true;
        }
        if (args.length == 1)
        {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null)
            {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }
            FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + " takes " + ChatColor.RED + player.getName() + ChatColor.AQUA + " up for a waltz!");
            return true;
        }
        FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + " spins around!");
        return true;
    }
}
