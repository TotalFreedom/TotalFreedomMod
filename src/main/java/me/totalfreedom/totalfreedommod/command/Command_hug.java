package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH, cooldown = 20)
@CommandParameters(description = "Hug someone <3", usage = "/<command> [player]")
public class Command_hug extends FreedomCommand
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
            FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + " hugs " + ChatColor.RED + player.getName() + ChatColor.AQUA + " " + ChatColor.LIGHT_PURPLE + "<3");
            return true;
        }
        FUtil.bcastMsg(ChatColor.RED + sender.getName() + ChatColor.AQUA + " looks like they really need a hug.");
        return true;
    }
}
