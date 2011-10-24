package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_qop extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (sender.isOp() || senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            boolean matched_player = false;
            for (Player p : Bukkit.matchPlayer(args[0]))
            {
                matched_player = true;

                TFM_Util.bcastMsg(String.format("(%s: Opping %s)", sender.getName(), p.getName()), ChatColor.GRAY);
                p.setOp(true);
                p.sendMessage(TotalFreedomMod.YOU_ARE_OP);
            }
            if (!matched_player)
            {
                sender.sendMessage("No targets matched.");
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
