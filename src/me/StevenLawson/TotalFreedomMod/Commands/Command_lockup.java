package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Random;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class Command_lockup extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
    	if (!(senderIsConsole && TotalFreedomMod.superUsers.contains(sender.getName().toLowerCase())))
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("all"))
            {
            	TFM_Util.adminAction(sender.getName(), "Locking up all players", true);
            	
                for (Player p : server.getOnlinePlayers())
                {
                    startLockup(p);
                }
                TFM_Util.playerMsg(sender, "Locked up all players.");
            }
            else if (args[0].equalsIgnoreCase("purge"))
            {
            	TFM_Util.adminAction(sender.getName(), "Unlocking all players", true);
                for (Player p : server.getOnlinePlayers())
                {
                    cancelLockup(p);
                }
                TFM_Util.playerMsg(sender, "Unlocked all players.");
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
                TFM_Util.playerMsg(sender, "Locked up " + p.getName() + ".");
            }
            else if (args[1].equalsIgnoreCase("off"))
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
                TFM_Util.playerMsg(sender, "Unlocked " + p.getName() + ".");
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

    private void cancelLockup(TFM_UserInfo playerdata)
    {
        BukkitScheduler scheduler = server.getScheduler();
        int lockupScheduleID = playerdata.getLockupScheduleID();
        if (lockupScheduleID != -1)
        {
            scheduler.cancelTask(lockupScheduleID);
            playerdata.setLockupScheduleID(-1);
        }
    }

    private void cancelLockup(final Player p)
    {
        cancelLockup(TFM_UserInfo.getPlayerData(p));
    }

    private void startLockup(final Player p)
    {
        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);

        cancelLockup(playerdata);

        playerdata.setLockupScheduleID(server.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable()
        {
            private Random random = new Random();

            @Override
            public void run()
            {
                p.openWorkbench(null, true);

                Location l = p.getLocation().clone();
                l.setPitch(random.nextFloat() * 360.0f);
                l.setYaw(random.nextFloat() * 360.0f);
                p.teleport(l);
            }
        }, 0L, 5L));
    }
}
