package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Block target's minecraft input. This is evil, and I never should have wrote it.", usage = "/<command> <all | purge | <<partialname> on | off>>")
public class Command_lockup extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("all"))
            {
                FUtil.adminAction(sender.getName(), "Locking up all players", true);

                for (Player player : server.getOnlinePlayers())
                {
                    startLockup(player);
                }
                msg("Locked up all players.");
            }
            else if (args[0].equalsIgnoreCase("purge"))
            {
                FUtil.adminAction(sender.getName(), "Unlocking all players", true);
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

                FUtil.adminAction(sender.getName(), "Locking up " + player.getName(), true);
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

                FUtil.adminAction(sender.getName(), "Unlocking " + player.getName(), true);
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
