package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Will Reset your tag and tab colour to default settings", usage = "/<command>")
public class Command_reset extends TFM_Command
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        final Player player = sender_p.getPlayer();

        String name = player.getName();

        if (TFM_AdminList.isAdminImpostor(player))
        {
            name = ChatColor.YELLOW + name;
            TFM_PlayerData.getPlayerData(player).setTag("&8[&eIMPOSTER&8]");
        }
        else if (TFM_Util.DEVELOPERS.contains(player.getName()))
        {
            name = ChatColor.DARK_PURPLE + name;
            TFM_PlayerData.getPlayerData(player).setTag("&8[&5Developer&8]");
        }
        else if (TFM_AdminList.isSeniorAdmin(player))
        {
            name = ChatColor.LIGHT_PURPLE + name;
            TFM_PlayerData.getPlayerData(player).setTag("&8[&dSenior Admin&8]");
        }
        else if (TFM_AdminList.isTelnetAdmin(player, true))
        {
            name = ChatColor.DARK_GREEN + name;
            TFM_PlayerData.getPlayerData(player).setTag("&8[&2Telnet Admin&8]");
        }
        else if (TFM_AdminList.isSuperAdmin(player))
        {
            name = ChatColor.AQUA + name;
            TFM_PlayerData.getPlayerData(player).setTag("&8[&BSuper Admin&8]");
        }
        else if (player.isOp())
        {
            TFM_PlayerData.getPlayerData(player).setTag("&8[&4OP&8]");
        }
        else
        {
            TFM_PlayerData.getPlayerData(player).setTag("&8[&fNON-OP&8]");
        }

        try
        {
            player.setPlayerListName(StringUtils.substring(name, 0, 16));
        }
        catch (IllegalArgumentException ex)
        {
        }

        return true;

    }
}
