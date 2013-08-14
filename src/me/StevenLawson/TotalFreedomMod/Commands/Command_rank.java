package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Shows your rank.", usage = "/<command>")
public class Command_rank extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole && args.length < 1)
        {
            for (Player player : server.getOnlinePlayers())
            {
                playerMsg(player.getName() + " is " + TFM_Util.getRank(player));
            }
            return true;
        }

        if (args.length > 1)
        {
            return false;
        }

        if (args.length == 0)
        {
            playerMsg(sender.getName() + " is " + TFM_Util.getRank(sender), ChatColor.AQUA);
            return true;
        }

        Player player;
        try
        {
            player = getPlayer(args[0]);
        }
        catch (PlayerNotFoundException ex)
        {
            sender.sendMessage(ex.getMessage());
            return true;
        }

        playerMsg(player.getName() + " is " + TFM_Util.getRank(player), ChatColor.AQUA);

        return true;
    }
}
