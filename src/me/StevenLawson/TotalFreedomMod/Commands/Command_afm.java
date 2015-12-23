package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.Config.TFM_MainConfig;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.TFM_CommandBlocker;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_PermbanList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerList;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Shows information about AlexFreedomMod or reloads it", usage = "/<command> [reload]")
public class Command_afm extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if (!args[0].equals("reload"))
            {
                return false;
            }

            if (!TFM_AdminList.isSuperAdmin(sender))
            {
                playerMsg(TFM_Command.MSG_NO_PERMS);
                return true;
            }

            TFM_MainConfig.load();
            TFM_AdminList.load();
            TFM_PermbanList.load();
            TFM_PlayerList.load();
            TFM_BanManager.load();
            TFM_CommandBlocker.load();

            final String message = String.format("%s v%s.%s reloaded.",
                    TotalFreedomMod.pluginName,
                    TotalFreedomMod.pluginVersion,
                    TotalFreedomMod.buildNumber);

            playerMsg(message);
            TFM_Log.info(message);
            return true;
        }

        playerMsg("AlexFreedom main plugin for 'AlexFreedom', remastered version of TFM", ChatColor.GREEN);
        playerMsg(String.format("Version "
                + ChatColor.BLUE + "%s.%s" + ChatColor.GREEN + ", built "
                + ChatColor.BLUE + "%s" + ChatColor.GREEN + " by "
                + ChatColor.BLUE + "%s" + ChatColor.GREEN + ".",
                TotalFreedomMod.pluginVersion,
                TotalFreedomMod.buildNumber,
                TotalFreedomMod.buildDate,
                TotalFreedomMod.buildCreator), ChatColor.GREEN);
        playerMsg("Running on " + TFM_ConfigEntry.SERVER_NAME.getString() + ".", ChatColor.GREEN);
        playerMsg("Created by Savnith.", ChatColor.GREEN);
        playerMsg("Visit " + ChatColor.AQUA + "http://alexfreedomremade.boards.net/" + ChatColor.YELLOW + " for more information on AlexFreedom!", ChatColor.GREEN);

        return true;
    }
}
