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
@CommandParameters(description = "Shows information about ImmaFreedom or reloads it", usage = "/<command> [reload]")
public class Command_ifm extends TFM_Command
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

            final String message = String.format("%s v%s reloaded.",
                    TotalFreedomMod.pluginName,
                    TotalFreedomMod.pluginVersion);

            playerMsg(message);
            TFM_Log.info(message);
            return true;
        }

        TotalFreedomMod.BuildProperties build = TotalFreedomMod.build;
        playerMsg("ImmaFreedomMod for 'ImmaFreedom', an associated, all-op server.", ChatColor.GOLD);
        playerMsg(String.format("Version "
                + ChatColor.BLUE + "%s.%s " + ChatColor.GOLD + "("
                + ChatColor.BLUE + "%s" + ChatColor.GOLD + ")",
                TotalFreedomMod.pluginVersion,
                build.number,
                build.head), ChatColor.GOLD);
        playerMsg(String.format("Compiled "
                + ChatColor.BLUE + "%s" + ChatColor.GOLD + " by "
                + ChatColor.BLUE + "%s",
                build.date,
                build.builder), ChatColor.GOLD);
        playerMsg("Running on " + TFM_ConfigEntry.SERVER_NAME.getString() + ".", ChatColor.GOLD);
        playerMsg("Created by Madgeek1450 and Prozza and later edited by aggelosQQ, AwesomePinch, AndySixx, Savnith, and tylerhyperHD", ChatColor.GOLD);
        playerMsg("Visit " + ChatColor.AQUA + "http://immafreedom.eu/website" + ChatColor.GREEN + " for more information.", ChatColor.GREEN);

        return true;
    }
}
