package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Superadmin command - Tempban a player for 5 minutes.", usage = "/<command> <player>")
public class Command_noob extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
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
    	
        server.dispatchCommand(sender, "tempban " + p.getName() + " 5m" + " You have broke a rule(s), please read totalfreedom.me, you'll be unbanned in 5 minutes.");

        return true;
    }
}