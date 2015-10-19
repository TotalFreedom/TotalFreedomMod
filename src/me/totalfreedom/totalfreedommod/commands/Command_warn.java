package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.admin.AdminList;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Warns a player.", usage = "/<command> <player> <reason>")
public class Command_warn extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 2)
        {
            return false;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            playerMsg(PLAYER_NOT_FOUND);
            return true;
        }

        if (sender instanceof Player)
        {
            if (player.equals(sender_p))
            {
                playerMsg(ChatColor.RED + "Please, don't try to warn yourself.");
                return true;
            }
        }

        if (plugin.al.isAdmin(player))
        {
            playerMsg(ChatColor.RED + "You can not warn admins");
            return true;
        }

        String warnReason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");

        playerMsg(ChatColor.RED + "[WARNING] " + warnReason);
        playerMsg(ChatColor.GREEN + "You have successfully warned " + player.getName());

        plugin.pl.getPlayer(player).incrementWarnings();

        return true;
    }
}
