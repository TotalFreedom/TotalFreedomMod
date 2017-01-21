package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.banning.Ban;
import static me.totalfreedom.totalfreedommod.command.FreedomCommand.PLAYER_NOT_FOUND;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SYS_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Kick a player.", usage = "/<command> <player> [reason]", aliases = "k")
public class Command_urlyfeisover extends FreedomCommand
{

    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }
            


        String reason = null;
        if (args.length > 1)
        {
            reason = StringUtils.join(args, " ", 1, args.length);
        }

        StringBuilder builder = new StringBuilder()
                 
                .append(ChatColor.RED).append("YOUR LYFE IS FUCKING OVER BITCH.")
                .append("\n").append(ChatColor.RED).append("Fucked By: ").append(ChatColor.GOLD).append(sender.getName()); 
           Admin admin = getAdmin(player);
        if (admin != null)
        {
            FUtil.adminAction(sender.getName(), "Removing " + player.getName() + " from the admin list", true);
            plugin.al.removeAdmin(admin);
        }
        if (reason != null)
        {
                    final StringBuilder bcast = new StringBuilder()
                .append(ChatColor.RED)
                .append("Banning: ")
                .append(player.getName())
                .append(", IP: ");
                plugin.bm.addBan(Ban.forPlayerFuzzy(player, sender, null, reason));
        if (reason != null)
        {
            bcast.append(" - Reason: ").append(ChatColor.YELLOW).append(reason);
        }
        FUtil.bcastMsg(bcast.toString());
        }
        else
        {
            FUtil.adminAction(sender.getName(), "Kicking " + player.getName(), true);
        }

        player.kickPlayer(builder.toString());
        return true;
    }

}
