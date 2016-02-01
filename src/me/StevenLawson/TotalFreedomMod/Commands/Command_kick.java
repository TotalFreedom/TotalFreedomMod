package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "For the bad Superadmins", usage = "/<command> <playername>")
public class Command_kick extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 1)
        {
            return false;
        }
        
        Player player = getPlayer(args[0]);
        
        if (player == null)
        {
            sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
            return true;
        }
        
        String reason = null;
        if (args.length >= 2)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        }
        
    TFM_Util.adminAction(sender.getName(), "Kicking " + player.getName() + " from the server", true);
    player.kickPlayer(ChatColor.RED + "You have been kicked from the server" + (reason != null ? ("\nReason: " + ChatColor.YELLOW + reason) : ""));
    
    return true;
    }
}
