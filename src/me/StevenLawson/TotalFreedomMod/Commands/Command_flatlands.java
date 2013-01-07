package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.ALL, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = true)
public class Command_flatlands extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (TotalFreedomMod.generateFlatlands)
        {
            TFM_Util.gotoWorld(sender, "flatlands");
        }
        else
        {
            playerMsg("Flatlands is currently disabled.");
        }
        return true;
    }
}
