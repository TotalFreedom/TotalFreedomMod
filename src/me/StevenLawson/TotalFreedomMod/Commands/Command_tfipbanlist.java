package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows all banned IPs. Superadmins may optionally use 'purge' to clear the list.", usage = "/<command> [purge]")
public class Command_tfipbanlist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("purge"))
            {
                if (senderIsConsole || TFM_AdminList.isSuperAdmin(sender))
                {
                    try
                    {
                        TFM_BanManager.purgeIpBans();
                        TFM_Util.adminAction(sender.getName(), "Purging the IP ban list", true);

                        sender.sendMessage(ChatColor.GRAY + "IP ban list has been purged.");
                    }
                    catch (Exception ex)
                    {
                        TFM_Log.severe(ex);
                    }

                    return true;
                }
                else
                {
                    playerMsg("You do not have permission to purge the IP ban list, you may only view it.");
                }
            }
        }

        playerMsg(TFM_BanManager.getIpBanList().size() + " IPbans total");

        return true;
    }
}
