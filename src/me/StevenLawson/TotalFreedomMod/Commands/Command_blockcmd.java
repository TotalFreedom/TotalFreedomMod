package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Block all commands for a specific player.", usage = "/<command> <purge | <partialname>>", aliases = "blockcommands,blockcommand")
public class Command_blockcmd extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("purge"))
        {
            TFM_Util.adminAction(sender.getName(), "Unblocking commands for all players", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                if (playerdata.allCommandsBlocked())
                {
                    counter += 1;
                    playerdata.setCommandsBlocked(false);
                }
            }
            playerMsg("Unblocked commands for " + counter + " players.");
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            playerMsg(TFM_Command.PLAYER_NOT_FOUND);
            return true;
        }

        if (TFM_AdminList.isSuperAdmin(player))
        {
            playerMsg(player.getName() + " is a Superadmin, and cannot have their commands blocked.");
            return true;
        }

        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);

        playerdata.setCommandsBlocked(!playerdata.allCommandsBlocked());

        TFM_Util.adminAction(sender.getName(), (playerdata.allCommandsBlocked() ? "B" : "Unb") + "locking all commands for " + player.getName(), true);
        playerMsg((playerdata.allCommandsBlocked() ? "B" : "Unb") + "locked all commands.");

        return true;
    }
}
