package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to survival, or define someone's username to change theirs.", usage = "/<command> [partialname]")
public class Command_survival extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            if (args.length == 0)
            {
                playerMsg("When used from the console, you must define a target user to change gamemode on.");
                return true;
            }
        }

        Player p;
        p = sender_p;
        if (args.length != 0){
        if (args[0].equalsIgnoreCase("-a"))
        {
           if (!TFM_SuperadminList.isUserSuperadmin(sender))
           {
               sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
               return true;
           }
           
            for (Player player : server.getOnlinePlayers())
            {
                if(!TFM_SuperadminList.isUserSuperadmin(p))
                {
                player.setGameMode(GameMode.CREATIVE);
                }
            }
            
            Bukkit.broadcastMessage(ChatColor.AQUA + sender.getName() + " - Setting all non-admins gamemode to creative.");
               return true;
        }
        }
        
        if (args.length == 0)
        {
            p = sender_p;
        }
        else
        {
            if (senderIsConsole || TFM_SuperadminList.isUserSuperadmin(sender))
            {
                try
                {
                    p = getPlayer(args[0]);
                }
                catch (CantFindPlayerException ex)
                {
                    playerMsg(ex.getMessage(), ChatColor.RED);
                    return true;
                }
            }
            else
            {
                playerMsg("Only superadmins can change other user's gamemode.");
                return true;
            }
        }

        playerMsg("Setting " + p.getName() + " to game mode 'Survival'.");
        p.sendMessage(sender.getName() + " set your game mode to 'Survival'.");
        p.setGameMode(GameMode.SURVIVAL);

        return true;
    }
}
