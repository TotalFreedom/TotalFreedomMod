package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH, cooldown = 20)
@CommandParameters(description = "Hmm...", usage = "/<command> [player]")
public class Command_ragehayes extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (args.length > 1)
        {
            return false;
        }
        if (args.length == 1)
        {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null)
            {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }
            FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + " just accused "
                    + ChatColor.RED + player.getName() + ChatColor.AQUA + " of being " + ChatColor.RED + "RageHayes's accomplice!"
                    + ChatColor.AQUA + "(RageHayes cheats on SMP) Everyone call them out!");
            return true;
        }
        FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + " is an accomplice of "
                + ChatColor.RED + "RageHayes" + ChatColor.AQUA + ", the one who cheated on " + ChatColor.RED + "SMP" + ChatColor.AQUA + "!");
        return true;
    }
}
