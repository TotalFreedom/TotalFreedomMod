package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                return true;
            }

            return false;
        }

        if (!senderIsConsole)
        {
            sender.sendMessage(ChatColor.GRAY + "This command may only be used from the console.");
            return true;
        }

        if (args.length < 2)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("add"))
        {
            Player p;
            try
            {
                p = getPlayer(args[1]);
            }
            catch (CantFindPlayerException ex)
            {
                sender.sendMessage(ex.getMessage());
                return true;
            }

            TFM_Util.adminAction(sender.getName(), "Adding " + p.getName() + " to the superadmin list.", true);

            TFM_SuperadminList.addSuperadmin(p);
        }
        else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove"))
        {
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
}
