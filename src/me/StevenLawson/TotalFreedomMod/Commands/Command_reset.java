package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "This command will reset your tag and prefix to the defaults", usage = "/<command>")
public class Command_reset extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (TFM_Util.DEVELOPERS.contains(sender_p.getName()))
        {
            sender_p.setPlayerListName(ChatColor.DARK_PURPLE + sender_p.getName());
            TFM_PlayerData.getPlayerData(sender_p).setTag("&8[&5Developer&8]");
        }
        else if (TFM_AdminList.isSeniorAdmin(sender_p))
        {
            sender_p.setPlayerListName(ChatColor.LIGHT_PURPLE + sender_p.getName());
            TFM_PlayerData.getPlayerData(sender_p).setTag("&8[&dSenior Admin&8]");
        }
        else if (TFM_AdminList.isTelnetAdmin(sender_p, true))
        {
            sender_p.setPlayerListName(ChatColor.DARK_GREEN + sender_p.getName());
            TFM_PlayerData.getPlayerData(sender_p).setTag("&8[&2Telnet Admin&8]");
        }
        else if (TFM_AdminList.isSuperAdmin(sender_p))
        {
            sender_p.setPlayerListName(ChatColor.AQUA + sender_p.getName());
            TFM_PlayerData.getPlayerData(sender_p).setTag("&8[&BSuper Admin&8]");

        }
        else if (TFM_AdminList.isAdminImpostor(sender_p))
        {
            sender_p.setPlayerListName(ChatColor.YELLOW + sender_p.getName());
            TFM_PlayerData.getPlayerData(sender_p).setTag("&8[&eIMPOSTER&8]");
        }
        else if (sender_p.isOp())
        {
            TFM_PlayerData.getPlayerData(sender_p).setTag("&8[&rOP&8]");
        }
        else
        {
            TFM_PlayerData.getPlayerData(sender_p).setTag("&8[&7NON-OP&8]");
        }
        return true;

    }
}
