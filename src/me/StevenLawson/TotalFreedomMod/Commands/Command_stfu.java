package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Mutes a player with brute force.", usage = "/<command> [<player> [-s] | list | purge | all]", aliases = "mute")
public class Command_stfu extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0 || args.length > 2)
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
            playerMsg("Unmuted " + count + " players.");
        }
        else if (args[0].equalsIgnoreCase("all"))
        {
            TFM_Util.adminAction(sender.getName(), "Muting all non-Superadmins", true);

            TFM_PlayerData playerdata;
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!TFM_AdminList.isSuperAdmin(player))
                {
                    playerdata = TFM_PlayerData.getPlayerData(player);
                    playerdata.setMuted(true);
                    counter++;
                }
            }

            playerMsg("Muted " + counter + " players.");
        }
        else
        {
            final Player player = getPlayer(args[0]);

            if (player == null)
            {
                sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
                return true;
            }

            TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
            if (playerdata.isMuted())
            {
                TFM_Util.adminAction(sender.getName(), "Unmuting " + player.getName(), true);
                playerdata.setMuted(false);
                playerMsg("Unmuted " + player.getName());
            }
            else
            {
                if (!TFM_AdminList.isSuperAdmin(player))
                {
                    TFM_Util.adminAction(sender.getName(), "Muting " + player.getName(), true);
                    playerdata.setMuted(true);

                    if (args.length == 2 && args[1].equalsIgnoreCase("-s"))
                    {
                        Command_smite.smite(player);
                    }

                    playerMsg("Muted " + player.getName());
                }
                else
                {
                    playerMsg(player.getName() + " is a superadmin, and can't be muted.");
                }
            }
        }

        return true;
    }
}
