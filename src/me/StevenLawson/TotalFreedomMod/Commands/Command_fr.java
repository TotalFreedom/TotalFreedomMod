package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.ADMIN_LEVEL;
import me.StevenLawson.TotalFreedomMod.Commands.CommandPermissions.SOURCE_TYPE_ALLOWED;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_fr extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            TotalFreedomMod.allPlayersFrozen = !TotalFreedomMod.allPlayersFrozen;

            if (TotalFreedomMod.allPlayersFrozen)
            {
                TFM_Util.adminAction(sender.getName(), "Freezing all players", false);
                TotalFreedomMod.allPlayersFrozen = true;
                TotalFreedomMod.freezePurgeEventId = server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (TotalFreedomMod.freezePurgeEventId == 0)
                        {
                            TFM_Log.warning("Freeze autopurge task was improperly cancelled!");
                            return;
                        }

                        TFM_Util.adminAction("FreezeTimer", "Unfreezing all players", false);

                        TotalFreedomMod.allPlayersFrozen = false;
                        TotalFreedomMod.freezePurgeEventId = 0;
                    }
                }, 6000L); // five minutes in ticks: 20*60*5
                playerMsg("Players are now frozen.");
            }
            else
            {
                TFM_Util.adminAction(sender.getName(), "Unfreezing all players", false);
                TotalFreedomMod.allPlayersFrozen = false;
                if (TotalFreedomMod.freezePurgeEventId != 0)
                {
                    server.getScheduler().cancelTask(TotalFreedomMod.freezePurgeEventId);
                    TotalFreedomMod.freezePurgeEventId = 0;
                }
                playerMsg("Players are now free to move.");
            }
        }
        else
        {
            if (args[0].toLowerCase().equals("purge"))
            {
                TotalFreedomMod.allPlayersFrozen = false;

                for (Player p : server.getOnlinePlayers())
                {
                    TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);
                    playerdata.setFrozen(false);
                }

                TFM_Util.adminAction(sender.getName(), "Lifting all global and player freezes", false);
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
                    playerMsg(ex.getMessage(), ChatColor.RED);
                    return true;
                }

                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(p);
                playerdata.setFrozen(!playerdata.isFrozen());

                playerMsg(p.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                playerMsg(p, "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".", ChatColor.AQUA);
            }
        }

        return true;
    }
}
