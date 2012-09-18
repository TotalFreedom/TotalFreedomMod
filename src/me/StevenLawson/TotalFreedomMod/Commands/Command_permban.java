package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
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
            if (TotalFreedomMod.permbanned_players.size() > 0)
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

            if (TotalFreedomMod.permbanned_ips.size() > 0)
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

            return true;
        }

        if (!senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload"))
        {
            plugin.loadPermbanConfig();
            return true;
        }

        return false;
    }
}
