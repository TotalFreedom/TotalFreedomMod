package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "This is evil, and I never should have wrote it - blocks specified player's input.", usage = "/<command> <all | purge | <<partialname> on | off> [-q]>")
public class Command_lockup extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Boolean silent = (args[args.length - 1].equalsIgnoreCase("-q"));
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("all"))
            {
                FUtil.staffAction(sender.getName(), "Locking up all players", true);

                for (Player player : server.getOnlinePlayers())
                {
                    startLockup(player);
                }
                msg("Locked up all players.");
            }
            else if (args[0].equalsIgnoreCase("purge"))
            {
                FUtil.staffAction(sender.getName(), "Unlocking all players", true);
                for (Player player : server.getOnlinePlayers())
                {
                    cancelLockup(player);
                }

                msg("Unlocked all players.");
            }
            else
            {
                return false;
            }
        }
        else if (args.length == 2)
        {
            if (args[1].equalsIgnoreCase("on"))
            {
                final Player player = getPlayer(args[0]);

                if (player == null)
                {
                    sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }

                if (!silent)
                {
                    FUtil.staffAction(sender.getName(), "Locking up " + player.getName(), true);
                }
                startLockup(player);
                msg("Locked up " + player.getName() + ".");
            }
            else if ("off".equals(args[1]))
            {
                final Player player = getPlayer(args[0]);

                if (player == null)
                {
                    sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }

                if (!silent)
                {
                    FUtil.staffAction(sender.getName(), "Unlocking " + player.getName(), true);
                }
                cancelLockup(player);
                msg("Unlocked " + player.getName() + ".");
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    private void cancelLockup(FPlayer playerdata)
    {
        BukkitTask lockupScheduleId = playerdata.getLockupScheduleID();
        if (lockupScheduleId != null)
        {
            lockupScheduleId.cancel();
            playerdata.setLockedUp(false);
            playerdata.setLockupScheduleId(null);
        }
    }

    private void cancelLockup(final Player player)
    {
        cancelLockup(plugin.pl.getPlayer(player));
    }

    private void startLockup(final Player player)
    {
        final FPlayer playerdata = plugin.pl.getPlayer(player);

        cancelLockup(playerdata);

        playerdata.setLockedUp(true);
        playerdata.setLockupScheduleId(new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (player.isOnline())
                {
                    player.openInventory(player.getInventory());
                }
                else
                {
                    cancelLockup(playerdata);
                }
            }
        }.runTaskTimer(plugin, 0L, 5L));
    }
}
