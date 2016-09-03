package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Get a players gamemode", usage = "/<command> <player>", aliases = "ggm,getgm,isgmc")
public class Command_getgamemode extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
       if (args.length < 1) {
           return false;
       }
       
       final Player player = getPlayer(args[0]);
       
       if (player == null) {
           msg(ChatColor.GRAY + "Player not found!");
       }
       
       msg(ChatColor.RED + player.getName() + "'s gamemode is " + ChatColor.AQUA + player.getGameMode() + ChatColor.RED + "!");
    return true;       
    }
    
}
