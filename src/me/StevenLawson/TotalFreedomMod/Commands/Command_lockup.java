package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
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

                for (Player player : server.getOnlinePlayers())
                {
                    startLockup(player);
                }
                playerMsg("Locked up all players.");
            }
            else if (args[0].equalsIgnoreCase("purge"))
            {
                TFM_Util.adminAction(sender.getName(), "Unlocking all players", true);
                for (Player player : server.getOnlinePlayers())
                {
                    cancelLockup(player);
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
                final Player player = getPlayer(args[0]);

                if (player == null)
                {
                    sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Locking up " + player.getName(), true);
                startLockup(player);
                playerMsg("Locked up " + player.getName() + ".");
            }
            else if ("off".equals(args[1]))
            {
                final Player player = getPlayer(args[0]);

                if (player == null)
                {
                    sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Unlocking " + player.getName(), true);
                cancelLockup(player);
                playerMsg("Unlocked " + player.getName() + ".");
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

    private void cancelLockup(final Player player)
    {
        cancelLockup(TFM_PlayerData.getPlayerData(player));
    }

    private void startLockup(final Player player)
    {
        final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);

        cancelLockup(playerdata);

        playerdata.setLockupScheduleID(new BukkitRunnable()
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
