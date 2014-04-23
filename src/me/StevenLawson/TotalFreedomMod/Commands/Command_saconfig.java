package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_TwitterHandler;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manage superadmins.", usage = "/<command> <list | clean | <add|delete|info> <username>>")
public class Command_saconfig extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0 || args.length > 2)
        {
            return false;
        }

        if (args.length == 1)
        {
            if (args[0].equals("list"))
            {
                playerMsg("Superadmins: " + StringUtils.join(TFM_AdminList.getSuperNames(), ", "), ChatColor.GOLD);
                return true;
            }

            if (args[0].equals("clean"))
            {

                if (!TFM_AdminList.isSeniorAdmin(sender, true))
                {
                    playerMsg(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Cleaning superadmin list", true);
                TFM_AdminList.cleanSuperadminList(true);
                playerMsg("Superadmins: " + StringUtils.join(TFM_AdminList.getSuperNames(), ", "), ChatColor.YELLOW);
                return true;
            }

            return false;
        }

        if (args[0].equalsIgnoreCase("info"))
        {
            if (!TFM_AdminList.isSuperAdmin(sender))
            {
                playerMsg(TotalFreedomMod.MSG_NO_PERMS);
                return true;
            }

            TFM_Admin superadmin = TFM_AdminList.getEntry(args[1].toLowerCase());

            if (superadmin == null)
            {
                try
                {
                    superadmin = TFM_AdminList.getEntry(getPlayer(args[1]).getName().toLowerCase());
                }
                catch (PlayerNotFoundException ex)
                {
                }
            }

            if (superadmin == null)
            {
                playerMsg("Superadmin not found: " + args[1]);
            }
            else
            {
                playerMsg(superadmin.toString());
            }

            return true;
        }

        if (!senderIsConsole)
        {
            playerMsg("This command may only be used from the console.");
            return true;
        }

        if (args[0].equalsIgnoreCase("add"))
        {
            OfflinePlayer player;

            try
            {
                player = getPlayer(args[1]);
            }
            catch (PlayerNotFoundException ex)
            {
                final TFM_Admin superadmin = TFM_AdminList.getEntry(args[1]);

                if (superadmin == null)
                {
                    playerMsg(ex.getMessage(), ChatColor.RED);
                    return true;
                }

                player = Bukkit.getOfflinePlayer(superadmin.getLastLoginName());
            }

            TFM_Util.adminAction(sender.getName(), "Adding " + player.getName() + " to the superadmin list", true);
            TFM_AdminList.addSuperadmin(player);

            return true;
        }

        if (TFM_Util.isRemoveCommand(args[0]))
        {
            if (!TFM_AdminList.isSeniorAdmin(sender))
            {
                playerMsg(TotalFreedomMod.MSG_NO_PERMS);
                return true;
            }

            String targetName = args[1];

            try
            {
                targetName = getPlayer(targetName).getName();
            }
            catch (PlayerNotFoundException ex)
            {
            }

            if (!TFM_AdminList.getLowerSuperNames().contains(targetName.toLowerCase()))
            {
                playerMsg("Superadmin not found: " + targetName);
                return true;
            }

            TFM_Util.adminAction(sender.getName(), "Removing " + targetName + " from the superadmin list", true);
            TFM_AdminList.removeSuperadmin(Bukkit.getOfflinePlayer(targetName));

            // Twitterbot
            if (TFM_ConfigEntry.TWITTERBOT_ENABLED.getBoolean())
            {
                TFM_TwitterHandler.getInstance().delTwitterVerbose(targetName, sender);
            }
            return true;
        }
        return false;

    }
}
