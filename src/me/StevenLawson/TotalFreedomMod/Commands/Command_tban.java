package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily bans a player for five minutes.", usage = "/<command> <partialname>", aliases = "nope")
public class Command_tban extends TFM_Command {

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        Player p;
        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            playerMsg(ex.getMessage(), ChatColor.RED);
            return true;
        }

        TFM_Util.adminAction(sender.getName(), "NOPE: " + p.getName(), true);
        TFM_ServerInterface.banUsername(p.getName(), ChatColor.RED + "You have been temporarily banned for 5 minutes",
                sender.getName(), TFM_Util.parseDateOffset("5m"));
        p.kickPlayer(ChatColor.RED + "NOPE!\nYou have been temporarily banned for five minutes.");

        return true;
    }

}
