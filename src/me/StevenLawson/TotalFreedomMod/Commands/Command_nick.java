package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Bridge.TFM_EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Give yourself a nickname.", usage = "/<command> <nickname>", aliases = "nickname")
public class Command_nick extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            playerMsg(ChatColor.RED + "You didn't provide a nickname!");
        }
        if (args.length > 1)
        {
            playerMsg(ChatColor.RED + "Too many command arguments!");
        }
        if ("off".equals(args[0]))
        {
            TFM_EssentialsBridge.setNickname(sender.getName(), null);
            playerMsg(ChatColor.GOLD + "You no longer have a nickname.");
            return true;
        }        
        final String nickPlain = ChatColor.stripColor(TFM_Util.colorize(args[0].trim()));
        final String nickInput = args[0];

        if (!nickPlain.matches("^[a-zA-Z_0-9\u00a7]+$"))
        {
            playerMsg(ChatColor.RED + "Your nickname contains invalid characters.");
            return true;
        }
        if (nickPlain.length() < 4 || nickPlain.length() > 30)
        {
            playerMsg("Your nickname must be between 4 and 30 characters long.");
            return true;
        }
        if (nickInput.contains("&k") || (nickInput.contains("&m") || (nickInput.contains("&n") || (nickInput.contains("&o")) || nickInput.contains("&0"))))
        {
            playerMsg(ChatColor.RED + "Your nickname contained forbidden chat formatting codes and as such, the forbidden codes have been removed from your nickname.");          
        }
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player == sender_p)
            {
                continue;
            }
            if (player.getName().equalsIgnoreCase(nickPlain) || ChatColor.stripColor(player.getDisplayName()).trim().equalsIgnoreCase(nickPlain))
            {
                playerMsg("That nickname is already in use.");
                return true;
            }
        } 
        String newNickname = nickInput.replace("&k", "").replace("&n", "").replace("&m", "").replace("&o", "").replace("&0", "").replace("&", "§");
        
            
        TFM_EssentialsBridge.setNickname(sender.getName(), newNickname);
        
        playerMsg(ChatColor.GOLD + "Your nickname is now " + newNickname + ChatColor.GOLD + ".");
        
        return true;
    }
    
    
}
