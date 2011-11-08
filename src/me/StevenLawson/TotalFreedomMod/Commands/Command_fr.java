package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_fr extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            if (args.length == 0)
            {
                TotalFreedomMod.allPlayersFrozen = !TotalFreedomMod.allPlayersFrozen;

                if (TotalFreedomMod.allPlayersFrozen)
                {
                    TotalFreedomMod.allPlayersFrozen = true;
                    sender.sendMessage("Players are now frozen.");
                    TFM_Util.bcastMsg(sender.getName() + " has temporarily frozen everyone on the server.", ChatColor.AQUA);
                }
                else
                {
                    TotalFreedomMod.allPlayersFrozen = false;
                    sender.sendMessage("Players are now free to move.");
                    TFM_Util.bcastMsg(sender.getName() + " has unfrozen everyone.", ChatColor.AQUA);
                }
            }
            else
            {
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

                TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);
                playerdata.setFrozen(!playerdata.isFrozen());

                sender.sendMessage(ChatColor.AQUA + p.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                p.sendMessage(ChatColor.AQUA + "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
