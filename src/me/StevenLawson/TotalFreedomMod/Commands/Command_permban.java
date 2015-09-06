package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PermbanList;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Manage permanently banned players and IPs.", usage = "/<command> reload")
public class Command_permban extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (!args[0].equalsIgnoreCase("reload"))
        {
            return false;
        }

        playerMsg("Reloading permban list...", ChatColor.RED);
        TFM_PermbanList.load();
        playerMsg("Reloaded permban list.");
        playerMsg(TFM_PermbanList.getPermbannedIps().size() + " IPs and "
                + TFM_PermbanList.getPermbannedPlayers().size() + " usernames loaded.");
        return true;
    }

}
