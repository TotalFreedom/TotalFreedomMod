package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerRank;
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
                playerMsg(player.getName() + " is " + TFM_PlayerRank.fromSender(player).getLoginMessage());
            }
            return true;
        }

        if (args.length > 1)
        {
            return false;
        }

        if (args.length == 0)
        {
            playerMsg(sender.getName() + " is " + TFM_PlayerRank.fromSender(sender).getLoginMessage(), ChatColor.AQUA);
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
            return true;
        }

        playerMsg(player.getName() + " is " + TFM_PlayerRank.fromSender(player).getLoginMessage(), ChatColor.AQUA);

        return true;
    }
}
