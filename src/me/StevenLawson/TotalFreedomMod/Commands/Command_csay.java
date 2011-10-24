package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_csay extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            String sender_name = sender.getName();

            if (sender_name.equalsIgnoreCase("remotebukkit"))
            {
                sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            }

            sender_name = sender_name.split("-")[0];

            StringBuilder outmessage_bldr = new StringBuilder();
            for (int i = 0; i < args.length; i++)
            {
                outmessage_bldr.append(args[i]).append(" ");
            }

            TFM_Util.bcastMsg(String.format("§7[CONSOLE]§f<§c%s§f> %s", sender_name, outmessage_bldr.toString().trim()));
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
