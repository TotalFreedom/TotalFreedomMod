package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Mutes a player with brute force.", usage = "/<command> [<player> | list | purge | all]", aliases = "mute")
public class Command_stfu extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            playerMsg("Muted players:");
            TFM_PlayerData info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = TFM_PlayerData.getPlayerData(mp);
                if (info.isMuted())
                {
                    playerMsg("- " + mp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                playerMsg("- none");
            }
        }
        else if (args[0].equalsIgnoreCase("purge"))
        {
            TFM_Util.adminAction(sender.getName(), "Unmuting all players.", true);
            TFM_PlayerData info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = TFM_PlayerData.getPlayerData(mp);
                if (info.isMuted())
                {
                    info.setMuted(false);
                    count++;
                }
            }
            if (TotalFreedomMod.mutePurgeEventId != 0)
            {
                server.getScheduler().cancelTask(TotalFreedomMod.mutePurgeEventId);
                TotalFreedomMod.mutePurgeEventId = 0;
            }
            playerMsg("Unmuted " + count + " players.");
        }
        else if (args[0].equalsIgnoreCase("all"))
        {
            TFM_Util.adminAction(sender.getName(), "Muting all non-Superadmins", true);

            TFM_PlayerData playerdata;
            int counter = 0;
            for (Player p : server.getOnlinePlayers())
            {
                if (!TFM_SuperadminList.isUserSuperadmin(p))
                {
                    playerdata = TFM_PlayerData.getPlayerData(p);
                    playerdata.setMuted(true);
                    counter++;
                }
            }

            TotalFreedomMod.mutePurgeEventId = server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    if (TotalFreedomMod.mutePurgeEventId == 0)
                    {
                        TFM_Log.warning("Mute autopurge task was improperly cancelled!");
                        return;
                    }

                    TFM_Util.adminAction("MuteTimer", "Unmuting all players", false);
                    for (Player p : server.getOnlinePlayers())
                    {
                        TFM_PlayerData.getPlayerData(p).setMuted(false);
                    }

                    TotalFreedomMod.mutePurgeEventId = 0;
                }
            }, 6000L); // five minutes in ticks: 20*60*5
            playerMsg("Muted " + counter + " players.");
        }
        else
        {
            Player p;
            try
            {
                p = getPlayer(args[0]);
            }
            catch (CantFindPlayerException ex)
            {
                sender.sendMessage(ex.getMessage());
                return true;
            }

            TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);
            if (playerdata.isMuted())
            {
                TFM_Util.adminAction(sender.getName(), "Unmuting " + p.getName(), true);
                playerdata.setMuted(false);
                playerMsg("Unmuted " + p.getName());
            }
            else
            {
                if (!TFM_SuperadminList.isUserSuperadmin(p))
                {
                    TFM_Util.adminAction(sender.getName(), "Muting " + p.getName(), true);
                    playerdata.setMuted(true);
                    playerMsg("Muted " + p.getName());
                }
                else
                {
                    playerMsg(p.getName() + " is a superadmin, and can't be muted.");
                }
            }
        }

        return true;
    }
}
