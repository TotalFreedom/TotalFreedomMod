package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Sync your time up with the real world time.", usage = "/<command> <on <utc_offset> | off>", aliases = "rt")
public class Command_realtime extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        VPlayer player = plugin.pv.getVerificationPlayer(playerSender);
        if (args.length == 0 || args.length > 2)
        {
            return false;
        }
        if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("on"))
            {
                int tz;
                try
                {
                    tz = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException ex)
                {
                    msg("Invalid UTC offset.");
                    return true;
                }
                if (FUtil.timeZoneOutOfBounds(tz))
                {
                    msg("Invalid UTC offset.");
                    return true;
                }
                player.setUtcOffset(tz);
                player.setRealTime(true);
                plugin.rt.enable(playerSender);
                plugin.pv.saveVerificationData(player);
                msg("Your in-game time is now synced with real time.");
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("off"))
        {
            if (!player.isRealTime())
            {
                msg("You aren't on real time.");
                return true;
            }
            player.setRealTime(false);
            msg("Your in-game time is no longer synced with real time.");
            plugin.rt.disable(playerSender);
            plugin.pv.saveVerificationData(player);
            return true;
        }
        return true;
    }
}
