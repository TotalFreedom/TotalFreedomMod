package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Superadmin;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.OP, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_saconfig extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if (args[0].equals("list"))
            {
                sender.sendMessage(ChatColor.GOLD + "Superadmins: " + StringUtils.join(TFM_SuperadminList.getSuperadminNames(), ", "));
            }
            else
            {
                if (!senderIsConsole)
                {
                    sender.sendMessage(ChatColor.GRAY + "This command may only be used from the console.");
                    return true;
                }

                if (args[0].equals("clean"))
                {
                    TFM_Util.adminAction(sender.getName(), "Cleaning superadmin list.", true);
                    TFM_SuperadminList.cleanSuperadminList(true);
                    sender.sendMessage(ChatColor.GOLD + "Superadmins: " + StringUtils.join(TFM_SuperadminList.getSuperadminNames(), ", "));
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
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                TFM_Superadmin superadmin = TFM_SuperadminList.getAdminEntry(args[1].toLowerCase());

                if (superadmin == null)
                {
                    try
                    {
                        superadmin = TFM_SuperadminList.getAdminEntry(getPlayer(args[1]).getName().toLowerCase());
                    }
                    catch (CantFindPlayerException ex)
                    {
                    }
                }

                if (superadmin == null)
                {
                    sender.sendMessage("Superadmin not found: " + args[1]);
                }
                else
                {
                    sender.sendMessage(ChatColor.GRAY + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', superadmin.toString())));
                }

                return true;
            }

            if (!senderIsConsole)
            {
                sender.sendMessage(ChatColor.GRAY + "This command may only be used from the console.");
                return true;
            }

            if (args[0].equalsIgnoreCase("add"))
            {
                Player p = null;
                String admin_name = null;

                try
                {
                    p = getPlayer(args[1]);
                }
                catch (CantFindPlayerException ex)
                {
                    TFM_Superadmin superadmin = TFM_SuperadminList.getAdminEntry(args[1].toLowerCase());
                    if (superadmin != null)
                    {
                        admin_name = superadmin.getName();
                    }
                    else
                    {
                        sender.sendMessage(ex.getMessage());
                        return true;
                    }
                }

                if (p != null)
                {
                    TFM_Util.adminAction(sender.getName(), "Adding " + p.getName() + " to the superadmin list.", true);
                    TFM_SuperadminList.addSuperadmin(p);
                }
                else if (admin_name != null)
                {
                    TFM_Util.adminAction(sender.getName(), "Adding " + admin_name + " to the superadmin list.", true);
                    TFM_SuperadminList.addSuperadmin(admin_name);
                }
            }
            else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove"))
            {
                if (!TFM_SuperadminList.isSeniorAdmin(sender))
                {
                    sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                String target_name = args[1];

                try
                {
                    target_name = getPlayer(target_name).getName();
                }
                catch (CantFindPlayerException ex)
                {
                }

                if (!TFM_SuperadminList.getSuperadminNames().contains(target_name.toLowerCase()))
                {
                    sender.sendMessage("Superadmin not found: " + target_name);
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Removing " + target_name + " from the superadmin list.", true);

                TFM_SuperadminList.removeSuperadmin(target_name);
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
