package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
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

        OfflinePlayer p = null;
        for (Player onlinePlayer : server.getOnlinePlayers())
        {
            if (args[0].equalsIgnoreCase(onlinePlayer.getName()))
            {
                p = onlinePlayer;
            }
        }

        // if the player is not online
        if (p == null)
        {
            if (TFM_SuperadminList.isUserSuperadmin(sender) || senderIsConsole)
            {
                p = server.getOfflinePlayer(args[0]);
            }
            else
            {
                playerMsg("That player is not online.");
                playerMsg("You don't have permissions to OP offline players.", ChatColor.YELLOW);
                return true;
            }
        }

        TFM_Util.adminAction(sender.getName(), "Opping " + p.getName(), false);
        p.setOp(true);

        return true;
    }
}
