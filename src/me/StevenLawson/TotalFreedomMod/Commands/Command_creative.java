package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.List;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_creative extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            if (args.length == 0)
            {
                sender.sendMessage("When used from the console, you must define a target user to change gamemode on.");
                return true;
            }
        }
        else
        {
            if (!sender.isOp())
            {
                sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
                return true;
            }
        }

        Player p;
        if (args.length == 0)
        {
            p = Bukkit.getPlayerExact(sender.getName());
        }
        else
        {
            List<Player> matches = Bukkit.matchPlayer(args[0]);
            if (matches.isEmpty())
            {
                sender.sendMessage("Can't find user " + args[0]);
                return true;
            }
            else
            {
                p = matches.get(0);
            }
        }

        sender.sendMessage("Setting " + p.getName() + " to game mode 'Creative'.");
        p.sendMessage(sender.getName() + " set your game mode to 'Creative'.");
        p.setGameMode(GameMode.CREATIVE);

        return true;
    }
}
