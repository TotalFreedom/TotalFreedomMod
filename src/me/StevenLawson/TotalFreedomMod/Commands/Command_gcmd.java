package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_gcmd extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 2)
        {
            return false;
        }

        Player p;
        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            sender.sendMessage(ex.getMessage());
            return true;
        }

        String outcommand = "";
        try
        {
            StringBuilder outcommand_bldr = new StringBuilder();
            for (int i = 1; i < args.length; i++)
            {
                outcommand_bldr.append(args[i]).append(" ");
            }
            outcommand = outcommand_bldr.toString().trim();
        }
        catch (Throwable ex)
        {
            sender.sendMessage(ChatColor.GRAY + "Error building command: " + ex.getMessage());
            return true;
        }

        try
        {
            playerMsg("Sending command as " + p.getName() + ": " + outcommand);
            if (server.dispatchCommand(p, outcommand))
            {
                playerMsg("Command sent.");
            }
            else
            {
                playerMsg("Unknown error sending command.");
            }
        }
        catch (Throwable ex)
        {
            playerMsg("Error sending command: " + ex.getMessage());
        }

        return true;
    }
}
