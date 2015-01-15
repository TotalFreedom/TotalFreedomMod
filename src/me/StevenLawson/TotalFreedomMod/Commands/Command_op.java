package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_DepreciationAggregator;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Makes a player operator", usage = "/<command> <playername>")
public class Command_op extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("everyone"))
        {
            playerMsg("Correct usage: /opall");
            return true;
        }

        OfflinePlayer player = null;
        for (Player onlinePlayer : server.getOnlinePlayers())
        {
            if (args[0].equalsIgnoreCase(onlinePlayer.getName()))
            {
                player = onlinePlayer;
            }
        }

        // if the player is not online
        if (player == null)
        {
            if (TFM_AdminList.isSuperAdmin(sender) || senderIsConsole)
            {
                player = TFM_DepreciationAggregator.getOfflinePlayer(server, args[0]);
            }
            else
            {
                playerMsg("That player is not online.");
                playerMsg("You don't have permissions to OP offline players.", ChatColor.YELLOW);
                return true;
            }
        }

        TFM_Util.adminAction(sender.getName(), "Opping " + player.getName(), false);
        player.setOp(true);

        return true;
    }
}
