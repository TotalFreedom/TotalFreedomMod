package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows all banned player names. Superadmins may optionally use 'purge' to clear the list.", usage = "/<command> [purge]")
public class Command_tfbanlist extends TFM_Command
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
                        TFM_Util.adminAction(sender.getName(), "Purging the ban list", true);
                        TFM_BanManager.purgeUuidBans();
                        sender.sendMessage(ChatColor.GRAY + "Ban list has been purged.");
                    }
                    catch (Exception ex)
                    {
                        TFM_Log.severe(ex);
                    }

                    return true;
                }
                else
                {
                    playerMsg("You do not have permission to purge the ban list, you may only view it.");
                }
            }
        }

        playerMsg(TFM_BanManager.getUuidBanList().size() + " UUID bans total");

        return true;
    }
}
