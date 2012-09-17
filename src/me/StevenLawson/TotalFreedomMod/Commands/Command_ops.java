package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_ops extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!(senderIsConsole || sender.isOp()))
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (args.length < 1 || args.length > 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            TFM_Util.playerMsg(sender, "Operators: " + StringUtils.join(server.getOperators(), ", "));

            return true;
        }

        if (args[0].equalsIgnoreCase("count"))
        {
            int onlineOPs = 0;
            int offlineOPs = 0;
            int totalOPs = 0;

            for (OfflinePlayer p : server.getOperators())
            {
                if (p.isOnline())
                {
                    onlineOPs++;
                }
                else
                {
                    offlineOPs++;
                }
                totalOPs++;
            }

            sender.sendMessage(ChatColor.GRAY + "Online OPs: " + onlineOPs);
            sender.sendMessage(ChatColor.GRAY + "Offline OPs: " + offlineOPs);
            sender.sendMessage(ChatColor.GRAY + "Total OPs: " + totalOPs);

            return true;
        }

        if (args[0].equalsIgnoreCase("purge"))
        {
            if (!senderIsConsole)
            {
                sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                return true;
            }

            TFM_Util.adminAction(sender.getName(), "Removing all operators", true);

            for (OfflinePlayer p : server.getOperators())
            {
                p.setOp(false);

                if (p.isOnline())
                {
                    p.getPlayer().sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);
                }
            }

            return true;
        }

        return true;
    }
}
