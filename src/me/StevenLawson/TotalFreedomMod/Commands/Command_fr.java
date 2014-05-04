package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Freeze players (toggles on and off).", usage = "/<command> [target | purge]")
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

                if (TotalFreedomMod.freezePurgeTask != null)
                {
                    TotalFreedomMod.freezePurgeTask.cancel();
                }
                TotalFreedomMod.freezePurgeTask = new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        TFM_Util.adminAction("FreezeTimer", "Unfreezing all players", false);
                        TotalFreedomMod.allPlayersFrozen = false;
                    }
                }.runTaskLater(plugin, 20L * 60L * 5L);

                playerMsg("Players are now frozen.");
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (!TFM_AdminList.isSuperAdmin(player))
                    {
                        TFM_Util.playerMsg(player, "You have been frozen due to rule breaker(s), you will be unfrozen very soon.", ChatColor.RED);
                    }
                }
            }
            else
            {
                TFM_Util.adminAction(sender.getName(), "Unfreezing all players", false);
                TotalFreedomMod.allPlayersFrozen = false;
                if (TotalFreedomMod.freezePurgeTask != null)
                {
                    TotalFreedomMod.freezePurgeTask.cancel();
                }
                playerMsg("Players are now free to move.");
            }
        }
        else
        {
            if (args[0].toLowerCase().equals("purge"))
            {
                TotalFreedomMod.allPlayersFrozen = false;
                if (TotalFreedomMod.freezePurgeTask != null)
                {
                    TotalFreedomMod.freezePurgeTask.cancel();
                }

                for (Player player : server.getOnlinePlayers())
                {
                    TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                    playerdata.setFrozen(false);
                }

                TFM_Util.adminAction(sender.getName(), "Lifting all global and player freezes", false);
            }
            else
            {
                final Player player = getPlayer(args[0]);

                if (player == null)
                {
                    playerMsg(TotalFreedomMod.PLAYER_NOT_FOUND, ChatColor.RED);
                    return true;
                }

                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                playerdata.setFrozen(!playerdata.isFrozen());

                playerMsg(player.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                playerMsg(player, "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".", ChatColor.AQUA);
            }
        }

        return true;
    }
}
