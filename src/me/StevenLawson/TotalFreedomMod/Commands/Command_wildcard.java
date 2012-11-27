package me.StevenLawson.TotalFreedomMod.Commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_wildcard extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args[0].equals("wildcard"))
        {
            sender.sendMessage("What the hell are you trying to do, you stupid idiot...");
            return true;
        }

        String base_command = StringUtils.join(args, " ");

        for (Player p : server.getOnlinePlayers())
        {
            String out_command = base_command.replaceAll("\\x3f", p.getName());
            sender.sendMessage("Running Command: " + out_command);
            server.dispatchCommand(sender, out_command);
        }

        return true;
    }
}
