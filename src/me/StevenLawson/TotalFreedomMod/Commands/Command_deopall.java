package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_deopall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (TFM_Util.isUserSuperadmin(sender, plugin) || senderIsConsole)
        {
            TFM_Util.bcastMsg(String.format("(%s: De-opping all players on server)", sender.getName()), ChatColor.YELLOW);

            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.setOp(false);
                p.sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);
            }

            if (args.length >= 1)
            {
                if (args[0].equalsIgnoreCase("purge"))
                {
                    sender.sendMessage(ChatColor.GRAY + "Purging ops.txt.");

                    for (OfflinePlayer p : Bukkit.getOperators())
                    {
                        p.setOp(false);
                    }
                }
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
