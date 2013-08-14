package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Deop a player.", usage = "/<command> <playername>")
public class Command_deop extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
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
            p = server.getOfflinePlayer(args[0]);
        }

        TFM_Util.adminAction(sender.getName(), "De-opping " + p.getName(), false);

        p.setOp(false);

        return true;
    }
}
