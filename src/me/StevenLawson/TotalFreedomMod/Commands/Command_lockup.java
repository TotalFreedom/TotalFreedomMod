package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE, block_host_console = true)
@CommandParameters(description = "Block target's minecraft input. This is evil, and I never should have wrote it.", usage = "/<command> <all | purge | <<partialname> on | off>>")
public class Command_lockup extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("all"))
            {
                TFM_Util.adminAction(sender.getName(), "Locking up all players", true);

                for (Player p : server.getOnlinePlayers())
                {
                    startLockup(p);
                }
                playerMsg("Locked up all players.");
            }
            else if (args[0].equalsIgnoreCase("purge"))
            {
                TFM_Util.adminAction(sender.getName(), "Unlocking all players", true);
                for (Player p : server.getOnlinePlayers())
                {
                    cancelLockup(p);
                }

                playerMsg("Unlocked all players.");
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
                final Player p;
                try
                {
                    p = getPlayer(args[0]);
                }
                catch (CantFindPlayerException ex)
                {
                    sender.sendMessage(ex.getMessage());
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Locking up " + p.getName(), true);
                startLockup(p);
                playerMsg("Locked up " + p.getName() + ".");
            }
            else if (TFM_Util.isStopCommand(args[1]))
            {
                final Player p;
                try
                {
                    p = getPlayer(args[0]);
                }
                catch (CantFindPlayerException ex)
                {
                    sender.sendMessage(ex.getMessage());
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Unlocking " + p.getName(), true);
                cancelLockup(p);
                playerMsg("Unlocked " + p.getName() + ".");
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

    private void cancelLockup(TFM_PlayerData playerdata)
    {
        BukkitTask lockupScheduleID = playerdata.getLockupScheduleID();
        if (lockupScheduleID != null)
        {
            lockupScheduleID.cancel();
            playerdata.setLockupScheduleID(null);
        }
    }

    private void cancelLockup(final Player p)
    {
        cancelLockup(TFM_PlayerData.getPlayerData(p));
    }

    private void startLockup(final Player p)
    {
        final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);

        cancelLockup(playerdata);

        playerdata.setLockupScheduleID(server.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                if (p.isOnline())
                {
                    p.openInventory(p.getInventory());
                }
                else
                {
                    cancelLockup(playerdata);
                }
            }
        }, 0L, 5L));
    }
}
