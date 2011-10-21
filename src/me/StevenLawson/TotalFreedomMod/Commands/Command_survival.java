package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_survival extends TFM_Command
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

        if (!sender.isOp())
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        Player p;
        if (args.length == 0)
        {
            p = sender_p;
        }
        else
        {
            try
            {
                p = getPlayer(args[0]);
            }
            catch (CantFindPlayerException ex)
            {
                sender.sendMessage(ex.getMessage());
                return true;
            }
        }

        sender.sendMessage("Setting " + p.getName() + " to game mode 'Survival'.");
        p.sendMessage(sender.getName() + " set your game mode to 'Survival'.");
        p.setGameMode(GameMode.SURVIVAL);

        return true;
    }
}
