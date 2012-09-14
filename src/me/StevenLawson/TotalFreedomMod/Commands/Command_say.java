package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_say extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }
        
        String message = TFM_Util.implodeStringList(" ", Arrays.asList(args));

        if (senderIsConsole && sender.getName().equals("Rcon"))
        {
            if (message.equals("WARNING: Server is restarting, you will be kicked"))
            {
                TFM_Util.bcastMsg("Server is going offline.", ChatColor.GRAY);

                for (Player p : server.getOnlinePlayers())
                {
                    p.kickPlayer("Server is going offline, come back in a few minutes.");
                }

                server.shutdown();

                return true;
            }
        }

        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender))
        {
            TFM_Util.bcastMsg(String.format("[Server:%s] %s", sender.getName(), message), ChatColor.LIGHT_PURPLE);
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
