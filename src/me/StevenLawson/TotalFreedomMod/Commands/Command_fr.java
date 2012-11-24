package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_fr extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            TotalFreedomMod.allPlayersFrozen = !TotalFreedomMod.allPlayersFrozen;

            if (TotalFreedomMod.allPlayersFrozen)
            {
                TotalFreedomMod.allPlayersFrozen = true;
                sender.sendMessage("Players are now frozen.");
                TFM_Util.adminAction(sender.getName(), "Freezing all players", false);
            }
            else
            {
                TotalFreedomMod.allPlayersFrozen = false;
                sender.sendMessage("Players are now free to move.");
                TFM_Util.adminAction(sender.getName(), "Unfreezing all players", false);
            }
        }
        else
        {
            if (args[0].toLowerCase().equals("purge"))
            {
                TotalFreedomMod.allPlayersFrozen = false;

                for (Player p : server.getOnlinePlayers())
                {
                    TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);
                    playerdata.setFrozen(false);
                }

                TFM_Util.adminAction(sender.getName(), "Lifting all global and player freezes", false);
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

        return true;
    }
}
