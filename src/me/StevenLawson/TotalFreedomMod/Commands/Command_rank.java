package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.ALL, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = true)
public class Command_rank extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole && args.length < 1)
        {
            for (Player p : server.getOnlinePlayers())
            {
                sender.sendMessage(p.getName() + " is " + TFM_Util.getRank(p));
            }
            return true;
        }

        if (args.length > 1)
        {
            return false;
        }

        if (args.length == 0)
        {
            TFM_Util.playerMsg(sender, sender.getName() + " is " + TFM_Util.getRank(sender), ChatColor.AQUA);
            return true;
        }

        Player p;
        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            sender.sendMessage(ex.getMessage());
            return true;
        }

        TFM_Util.playerMsg(sender, p.getName() + " is " + TFM_Util.getRank(p), ChatColor.AQUA);

        return true;
    }
}
