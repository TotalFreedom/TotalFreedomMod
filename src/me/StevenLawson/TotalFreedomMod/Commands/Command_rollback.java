package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_RollbackManager;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, block_host_console = true)
@CommandParameters(description = "Issues a rollback on a player", usage = "/<command> <[partialname] | purge [partialname] | purgeall>", aliases = "rb")
public class Command_rollback extends TFM_Command {

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 2)
        {
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("purgeall"))
        {
            TFM_Util.adminAction(sender.getName(), "Puring all rollback data", false);
            playerMsg("Purged entries for " + TFM_RollbackManager.purgeEntries() + " players.");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("purge"))
        {
            OfflinePlayer p;
            try
            {
                p = getPlayer(args[1]);
            }
            catch (CantFindPlayerException ex)
            {
                p = server.getOfflinePlayer(args[1]);
                if (!p.hasPlayedBefore())
                {
                    playerMsg("Player is not online, or never joined the server.", ChatColor.RED);
                    return true;
                }
            }

            if (!TFM_RollbackManager.canRollback(p.getName()))
            {
                playerMsg("No rollback data found for that player", ChatColor.RED);
            } else {
                playerMsg("Purged " + TFM_RollbackManager.purgeEntries(p.getName()) + " entries.");
                return true;
            }
        }

        if (args.length != 1)
        {
            return false;
        }

        OfflinePlayer p;
        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            p = server.getOfflinePlayer(args[0]);
            if (!p.hasPlayedBefore())
            {
                playerMsg("Player is not online, or never joined the server.", ChatColor.RED);
                return true;
            }
        }

        if (!TFM_RollbackManager.canRollback(p.getName()))
        {
            playerMsg("Player has no rollback data set.", ChatColor.RED);
            return true;
        }

        TFM_Util.adminAction(sender.getName(), "Rolling back player: " + p.getName(), false);
        playerMsg("Rolled back " + TFM_RollbackManager.rollback(p) + " blocks");
        return true;
    }

}
