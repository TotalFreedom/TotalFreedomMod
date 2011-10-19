package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_umd extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (MobDisguiseAPI.isDisguised(p))
                {
                    p.sendMessage(ChatColor.GRAY + "You have been undisguised by an administrator.");
                }

                MobDisguiseAPI.undisguisePlayer(p);
                MobDisguiseAPI.undisguisePlayerAsPlayer(p, "");
            }

            sender.sendMessage(ChatColor.GRAY + "All players have been undisguised.");
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
