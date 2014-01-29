package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manage operators", usage = "/<command> <count | list>")
public class Command_ops extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1 || args.length > 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            TFM_Util.playerMsg(sender, "Operators: " + TFM_Util.playerListToNames(server.getOperators()));
            return true;
        }

        if (args[0].equalsIgnoreCase("count"))
        {
            int onlineOPs = 0;
            int offlineOPs = 0;
            int totalOPs = 0;

            for (OfflinePlayer player : server.getOperators())
            {
                if (player.isOnline())
                {
                    onlineOPs++;
                }
                else
                {
                    offlineOPs++;
                }
                totalOPs++;
            }

            playerMsg("Online OPs: " + onlineOPs);
            playerMsg("Offline OPs: " + offlineOPs);
            playerMsg("Total OPs: " + totalOPs);

            return true;
        }

        return true;
    }
}
