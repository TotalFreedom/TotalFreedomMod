package me.StevenLawson.TotalFreedomMod.Commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Run any command on all users, username placeholder = ?.", usage = "/<command> [fluff] ? [fluff] ?")
public class Command_wildcard extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
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

        String base_command = StringUtils.join(args, " ");

        for (Player p : server.getOnlinePlayers())
        {
            String out_command = base_command.replaceAll("\\x3f", p.getName());
            playerMsg("Running Command: " + out_command);
            server.dispatchCommand(sender, out_command);
        }

        return true;
    }
}
