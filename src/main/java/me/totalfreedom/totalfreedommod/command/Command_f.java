package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH, cooldown = 20)
@CommandParameters(description = "Pay your respects..", usage = "/<command>")
public class Command_f extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (args.length != 0)
        {
            return false;
        }
        FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + " just payed their respects! Type " + ChatColor.RED + "F " + ChatColor.AQUA + "to pay your respects!");
        return true;
    }
}
