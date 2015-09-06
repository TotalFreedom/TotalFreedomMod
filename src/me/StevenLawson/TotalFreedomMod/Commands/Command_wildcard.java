package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_CommandBlocker;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Run any command on all users, username placeholder = ?.", usage = "/<command> [fluff] ? [fluff] ?")
public class Command_wildcard extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("wildcard"))
        {
            playerMsg("What the hell are you trying to do, you stupid idiot...", ChatColor.RED);
            return true;
        }
        if (args[0].equals("gtfo"))
        {
            playerMsg("Nice try", ChatColor.RED);
            return true;
        }
        if (args[0].equals("doom"))
        {
            playerMsg("Look, we all hate people, but this is not the way to deal with it, doom is evil enough!", ChatColor.RED);
            return true;
        }
        if (args[0].equals("saconfig"))
        {
            playerMsg("WOA, WTF are you trying to do???", ChatColor.RED);
            return true;
        }

        String baseCommand = StringUtils.join(args, " ");

        if (TFM_CommandBlocker.isCommandBlocked(baseCommand, sender))
        {
            // CommandBlocker handles messages and broadcasts
            return true;
        }

        for (Player player : server.getOnlinePlayers())
        {
            String out_command = baseCommand.replaceAll("\\x3f", player.getName());
            playerMsg("Running Command: " + out_command);
            server.dispatchCommand(sender, out_command);
        }

        return true;
    }
}
