package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_permban extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!sender.isOp())
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            dumplist(sender);
            return true;
        }

        if (!senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload"))
        {
            TFM_Util.playerMsg(sender, "Reloading permban list...", ChatColor.RED);
            plugin.loadPermbanConfig();
            dumplist(sender);
            return true;
        }

        return false;
    }

    private void dumplist(CommandSender sender)
    {
        if (TotalFreedomMod.permbanned_players.isEmpty())
        {
            TFM_Util.playerMsg(sender, "No permanently banned player names.");
        }
        else
        {
            TFM_Util.playerMsg(sender, TotalFreedomMod.permbanned_players.size() + " permanently banned players:");
            for (String player_name : TotalFreedomMod.permbanned_players)
            {
                TFM_Util.playerMsg(sender, "- " + player_name);
            }
        }

        if (TotalFreedomMod.permbanned_ips.isEmpty())
        {
            TFM_Util.playerMsg(sender, "No permanently banned IPs.");
        }
        else
        {
            TFM_Util.playerMsg(sender, TotalFreedomMod.permbanned_ips.size() + " permanently banned IPs:");
            for (String ip_address : TotalFreedomMod.permbanned_ips)
            {
                TFM_Util.playerMsg(sender, "- " + ip_address);
            }
        }
    }
}
