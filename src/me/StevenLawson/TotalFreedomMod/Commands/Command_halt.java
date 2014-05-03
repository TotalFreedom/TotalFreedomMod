package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Halts a player", usage = "/<command> <<partialname> | all | purge | list>")
public class Command_halt extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("all"))
        {
            TFM_Util.adminAction(sender.getName(), "Halting all non-superadmins.", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!TFM_AdminList.isSuperAdmin(player))
                {
                    TFM_PlayerData.getPlayerData(player).setHalted(true);
                    counter++;
                }
            }
            playerMsg("Halted " + counter + " players.");
            return true;
        }

        if (args[0].equalsIgnoreCase("purge"))
        {
            TFM_Util.adminAction(sender.getName(), "Unhalting all players.", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                if (TFM_PlayerData.getPlayerData(player).isHalted())
                {
                    playerdata.setHalted(false);
                    counter++;
                }
            }
            playerMsg("Unhalted " + counter + " players.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            TFM_PlayerData info;
            int count = 0;
            for (Player hp : server.getOnlinePlayers())
            {
                info = TFM_PlayerData.getPlayerData(hp);
                if (info.isHalted())
                {
                    if (count == 0)
                    {
                        playerMsg(sender, "Halted players:");
                    }
                    playerMsg("- " + hp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                playerMsg("There are currently no halted players.");
            }
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(TotalFreedomMod.PLAYER_NOT_FOUND);
            return true;
        }

        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
        if (!playerdata.isHalted())
        {
            TFM_Util.adminAction(sender.getName(), "Halting " + player.getName(), true);
            playerdata.setHalted(true);
            return true;
        }
        else
        {
            TFM_Util.adminAction(sender.getName(), "Unhalting " + player.getName(), true);
            playerdata.setHalted(false);
            return true;
        }
    }
}
