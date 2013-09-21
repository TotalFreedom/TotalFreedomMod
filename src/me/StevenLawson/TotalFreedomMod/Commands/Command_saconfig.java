package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Superadmin;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_TwitterHandler;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang3.StringUtils;
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
                if (sender instanceof Player && !TFM_SuperadminList.isSeniorAdmin(sender))
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

                if (!TFM_ConfigEntry.TWITTERBOT_ENABLED.getBoolean())
                {
                    return true;
                }

                // Twitterbot
                TFM_TwitterHandler twitterbot = TFM_TwitterHandler.getInstance();
                String reply = twitterbot.delTwitter(targetName);
                if ("ok".equals(reply))
                {
                    TFM_Util.adminAction(sender.getName(), "Removing " + targetName + " from TwitterBot", true);
                }
                else if ("disabled".equals(reply))
                {
                    TFM_Util.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
                    TFM_Util.playerMsg(sender, "TwitterBot has been temporarily disabled, please wait until it gets re-enabled", ChatColor.RED);
                }
                else if ("failed".equals(reply))
                {
                    TFM_Util.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
                    TFM_Util.playerMsg(sender, "There was a problem querying the database, please let a developer know.", ChatColor.RED);
                }
                else if ("false".equals(reply))
                {
                    TFM_Util.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
                    TFM_Util.playerMsg(sender, "There was a problem with the database, please let a developer know.", ChatColor.RED);
                }
                else if ("cannotauth".equals(reply))
                {
                    TFM_Util.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
                    TFM_Util.playerMsg(sender, "The database password is incorrect, please let a developer know.", ChatColor.RED);
                }
                else if ("notfound".equals(reply))
                {
                    TFM_Util.playerMsg(sender, targetName + " did not have a twitter handle registered to their name.", ChatColor.GREEN);
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
