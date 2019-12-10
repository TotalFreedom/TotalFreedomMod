package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Make other people your bitch.", usage = "/<command> <playername>")
public class Command_ride extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length < 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("toggle"))
        {
            VPlayer vPlayerSender = plugin.pv.getVerificationPlayer(playerSender);
            vPlayerSender.setRideToggle(!vPlayerSender.isRideToggle());
            plugin.pv.saveVerificationData(vPlayerSender);
            msg("Other players n" + (vPlayerSender.isRideToggle() ? "ow" : "o longer") + " have the ability to ride you.");
            return true;
        }

        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        final VPlayer vPlayer = plugin.pv.getVerificationPlayer(player);

        if (player == playerSender)
        {
            msg("You can't ride yourself. smh.", ChatColor.RED);
            return true;
        }

        if (!vPlayer.isRideToggle() && !isAdmin(sender))
        {
            msg("That player cannot be ridden.", ChatColor.RED);
            return true;
        }

        player.addPassenger(playerSender);

        return true;
    }
}
