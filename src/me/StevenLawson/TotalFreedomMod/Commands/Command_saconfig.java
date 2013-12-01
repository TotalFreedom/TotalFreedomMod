package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Superadmin;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_TwitterHandler;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
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
        if (args.length == 1)
        {
            if (args[0].equals("list"))
            {
                playerMsg("Superadmins: " + StringUtils.join(TFM_SuperadminList.getSuperadminNames(), ", "), ChatColor.GOLD);
            }
            else
            {
                if (!TFM_SuperadminList.isSeniorAdmin(sender, true))
                {
                    playerMsg(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                if (args[0].equals("clean"))
                {
                    TFM_Util.adminAction(sender.getName(), "Cleaning superadmin list", true);
                    TFM_SuperadminList.cleanSuperadminList(true);
                    playerMsg("Superadmins: " + StringUtils.join(TFM_SuperadminList.getSuperadminNames(), ", "), ChatColor.YELLOW);
                }
                else
                {
                    return false;
                }

                return true;
            }

            return true;
        }
        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("info"))
            {
                if (!TFM_SuperadminList.isUserSuperadmin(sender))
                {
                    playerMsg(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                TFM_Superadmin superadmin = TFM_SuperadminList.getAdminEntry(args[1].toLowerCase());

                if (superadmin == null)
                {
                    try
                    {
                        superadmin = TFM_SuperadminList.getAdminEntry(getPlayer(args[1]).getName().toLowerCase());
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
                    playerMsg(ChatColor.stripColor(TFM_Util.colorize(superadmin.toString())));
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
                Player player = null;
                String admin_name = null;

                try
                {
                    player = getPlayer(args[1]);
                }
                catch (PlayerNotFoundException ex)
                {
                    TFM_Superadmin superadmin = TFM_SuperadminList.getAdminEntry(args[1].toLowerCase());
                    if (superadmin != null)
                    {
                        admin_name = superadmin.getName();
                    }
                    else
                    {
                        playerMsg(ex.getMessage(), ChatColor.RED);
                        return true;
                    }
                }

                if (player != null)
                {
                    TFM_Util.adminAction(sender.getName(), "Adding " + player.getName() + " to the superadmin list.", true);
                    TFM_SuperadminList.addSuperadmin(player);
                }
                else if (admin_name != null)
                {
                    TFM_Util.adminAction(sender.getName(), "Adding " + admin_name + " to the superadmin list.", true);
                    TFM_SuperadminList.addSuperadmin(admin_name);
                }
            }
            else if (TFM_Util.isRemoveCommand(args[0]))
            {
                if (!TFM_SuperadminList.isSeniorAdmin(sender))
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

                if (!TFM_SuperadminList.getSuperadminNames().contains(targetName.toLowerCase()))
                {
                    playerMsg("Superadmin not found: " + targetName);
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Removing " + targetName + " from the superadmin list", true);
                TFM_SuperadminList.removeSuperadmin(targetName);

                // Twitterbot
                if (TFM_ConfigEntry.TWITTERBOT_ENABLED.getBoolean())
                {
                    TFM_TwitterHandler.getInstance().delTwitterVerbose(targetName, sender);
                }
            }
            else
            {
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
