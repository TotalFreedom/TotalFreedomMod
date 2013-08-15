package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily bans a player for five minutes.", usage = "/<command> <partialname>", aliases = "noob")
public class Command_tban extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        Player player;
        try
        {
            player = getPlayer(args[0]);
        }
        catch (PlayerNotFoundException ex)
        {
            playerMsg(ex.getMessage(), ChatColor.RED);
            return true;
        }

        TFM_Util.adminAction(sender.getName(), "Tempbanning: " + player.getName() + " for 5 minutes.", true);
        TFM_ServerInterface.banUsername(player.getName(), ChatColor.RED + "You have been temporarily banned for 5 minutes.", sender.getName(), TFM_Util.parseDateOffset("5m"));
        player.kickPlayer(ChatColor.RED + "You have been temporarily banned for five minutes. Please read totalfreedom.me for more info.");

        return true;
    }
}
