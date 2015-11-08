package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Op everyone on the server, optionally change everyone's gamemode at the same time.", usage = "/<command> [-c | -s] [-a]")
public class Command_opall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player.isOp()) 
            {
                sender.sendMessage(ChatColor.RED + "All current online players are already OP");  
            }
        }
        
        TFM_Util.adminAction(sender.getName(), "Opping all players on the server", false);

        boolean doSetGamemode = false;
        GameMode targetGamemode = GameMode.CREATIVE;
        if (args.length != 0)
        {
            if (args[0].equals("-c"))
            {
                doSetGamemode = true;
                targetGamemode = GameMode.CREATIVE;
            }
            else if (args[0].equals("-s"))
            {
                doSetGamemode = true;
                targetGamemode = GameMode.SURVIVAL;
            }
            else if (args[0].equals("-a"))
            {
                doSetGamemode = true;
                targetGamemode = GameMode.ADVENTURE;
            }

        }

        for (Player player : server.getOnlinePlayers())
        {
            player.setOp(true);
            player.sendMessage(TFM_Command.YOU_ARE_OP);

            if (doSetGamemode)
            {
                player.setGameMode(targetGamemode);
            }
        }

        return true;
    }
}
