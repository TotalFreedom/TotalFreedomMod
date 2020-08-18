package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Warns the specified player.", usage = "/<command> [-q] <player> <reason>")
public class Command_warn extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        boolean quiet = args[0].equalsIgnoreCase("-q");
        if (quiet)
        {
            args = org.apache.commons.lang3.ArrayUtils.subarray(args, 1, args.length);

            if (args.length < 1)
            {
                return false;
            }
        }

        Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        if (playerSender == player)
        {
            msg(ChatColor.RED + "Please, don't try to warn yourself.");
            return true;
        }

        if (plugin.sl.isStaff(player))
        {
            msg(ChatColor.RED + "You can not warn staff");
            return true;
        }

        String warnReason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        player.sendTitle(ChatColor.RED + "You've been warned.", ChatColor.YELLOW + "Reason: " + warnReason, 20, 100, 60);
        msg(ChatColor.GREEN + "You have successfully warned " + player.getName());

        if (quiet)
        {
            msg("Warned " + player.getName() + " quietly");
            return true;
        }

        msg(player, ChatColor.RED + "[WARNING] You received a warning from " + sender.getName() + ": " + warnReason);
        String staffNotice = ChatColor.RED +
                sender.getName() +
                " - " +
                "Warning: " +
                player.getName() +
                " - Reason: " +
                ChatColor.YELLOW +
                warnReason;
        plugin.sl.messageAllStaff(staffNotice);
        plugin.pl.getPlayer(player).incrementWarnings();
        return true;
    }
}