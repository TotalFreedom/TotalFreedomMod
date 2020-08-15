package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Unmutes a player", usage = "/<command> [-q] <player>")
public class Command_unmute extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        // -q option (shadowmute)
        boolean quiet = args[0].equals("-q");
        if (quiet)
        {
            args = ArrayUtils.subarray(args, 1, args.length);

            if (args.length < 1)
            {
                return false;
            }
        }

        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        FPlayer playerdata = plugin.pl.getPlayer(player);
        if (playerdata.isMuted())
        {
            playerdata.setMuted(false);
            player.sendTitle(ChatColor.RED + "You've been unmuted.", ChatColor.YELLOW + "Be sure to follow the rules!", 20, 100, 60);

            if (quiet)
            {
                msg("Unmuted " + player.getName() + " quietly");
                return true;
            }

            FUtil.staffAction(sender.getName(), "Unmuting " + player.getName(), true);
            msg("Unmuted " + player.getName());
            msg(player, "You have been unmuted.", ChatColor.RED);
        }
        else
        {
            msg(ChatColor.RED + "That player is not muted.");
        }
        return true;
    }
}