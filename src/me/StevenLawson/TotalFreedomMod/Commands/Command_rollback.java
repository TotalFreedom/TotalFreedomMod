package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_RollbackManager;
import me.StevenLawson.TotalFreedomMod.TFM_UserList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, block_host_console = true)
@CommandParameters(description = "Issues a rollback on a player", usage = "/<command> <[partialname] | purge [partialname] | purgeall>", aliases = "rb")
public class Command_rollback extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("purgeall"))
            {
                TFM_Util.adminAction(sender.getName(), "Purging all rollback history.", false);
                playerMsg("Purged all rollback history for " + TFM_RollbackManager.purgeEntries() + " players.");
            }
            else
            {
                String playerName = getPlayerName(args[0]);
                TFM_Util.adminAction(sender.getName(), "Rolling back player: " + playerName, false);
                playerMsg("Rolled back " + TFM_RollbackManager.purgeEntries(playerName) + " edits for " + playerName + ".");
            }
        }
        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("purge"))
            {
                String playerName = getPlayerName(args[1]);
                playerMsg("Purged " + TFM_RollbackManager.purgeEntries(playerName) + " rollback history entries for " + playerName + ".");
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }

        return true;
    }

    private String getPlayerName(String playerNameInput)
    {
        String playerName = null;

        try
        {
            Player player = getPlayer(playerNameInput);
            if (player != null)
            {
                playerName = player.getName();
            }
        }
        catch (CantFindPlayerException ex)
        {
        }

        if (playerName == null)
        {
            playerName = TFM_UserList.getInstance(plugin).searchByPartialName(playerNameInput);
        }

        return playerName;
    }
}
