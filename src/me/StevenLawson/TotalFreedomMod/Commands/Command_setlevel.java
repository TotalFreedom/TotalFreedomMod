package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Sets your expierence level.", usage = "/<command> [level]")
public class Command_setlevel extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        int new_level;

        try
        {
            new_level = Integer.parseInt(args[0]);

            if (new_level < 0)
            {
                new_level = 0;
            }
            else if (new_level > 50)
            {
                new_level = 50;
            }
        }
        catch (NumberFormatException ex)
        {
            playerMsg("Invalid level.", ChatColor.RED);
            return true;
        }

        sender_p.setLevel(new_level);

        playerMsg("You have been set to level " + Integer.toString(new_level), ChatColor.AQUA);

        return true;
    }
}
