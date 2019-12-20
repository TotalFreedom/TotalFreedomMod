package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH, cooldown = 20)
@CommandParameters(description = "Gives someone a high five!", usage = "/<command> [player]")
public class Command_highfive extends FreedomCommand
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
            FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + " gives a high five to " + ChatColor.RED + player.getName() + ChatColor.AQUA + "!");
            FUtil.bcastMsg(ChatColor.AQUA + "Good job " + ChatColor.RED + sender.getName() + ChatColor.AQUA + "!");
            return true;
        }
        FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + " raises their palm for a high five");
        FUtil.bcastMsg(ChatColor.AQUA + "but there was noone to answer.");
        return true;
    }
}
