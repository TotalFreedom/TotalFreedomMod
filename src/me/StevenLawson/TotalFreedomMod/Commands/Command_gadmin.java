package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(
        description = "Lists the list of real names on the server, and there nickname.",
        usage = "/<command> [list]")
public class Command_gadmin extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        String mode = args[0].toLowerCase();

        if (mode.equals("list"))
        {
            playerMsg("[ Real Name ] : [ Display Name ]:");
        }

        for (Player p : server.getOnlinePlayers())
        {
            if (mode.equals("list"))
            {
                sender.sendMessage(ChatColor.GRAY + String.format("[ %s ] : [ %s ]",
                        p.getName(),
                        ChatColor.stripColor(p.getDisplayName())));
            }
        }
        if (!mode.equals("list"))
        {
            playerMsg("Invalid hash.", ChatColor.RED);
        }

        return true;
    }
}
