package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
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
                plugin.allPlayersFrozen = !plugin.allPlayersFrozen;

                if (plugin.allPlayersFrozen)
                {
                    plugin.allPlayersFrozen = true;
                    sender.sendMessage("Players are now frozen.");
                    TFM_Util.tfm_broadcastMessage(sender.getName() + " has temporarily frozen everyone on the server.", ChatColor.AQUA);
                }
                else
                {
                    plugin.allPlayersFrozen = false;
                    sender.sendMessage("Players are now free to move.");
                    TFM_Util.tfm_broadcastMessage(sender.getName() + " has unfrozen everyone.", ChatColor.AQUA);
                }
            }
            else
            {
                Player p;
                List<Player> matches = Bukkit.matchPlayer(args[0]);
                if (matches.isEmpty())
                {
                    sender.sendMessage("Can't find user " + args[0]);
                    return true;
                }
                else
                {
                    p = matches.get(0);
                }

                TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p, plugin);
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
