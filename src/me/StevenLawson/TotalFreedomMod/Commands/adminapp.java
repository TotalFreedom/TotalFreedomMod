package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Shows you how to become a admin.", usage = "/<command>")
public class Command_adminapp extends TFM_Command
{
    
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        playerMsg(ChatColor.AQUA + "The following is accurate as of 13/05/2013");
        playerMsg(ChatColor.AQUA + "To apply for admin you need to go to the forums at http://www.totalfreedom.boards.net");
        playerMsg(ChatColor.AQUA + "Then read the requirements.");
        playerMsg(ChatColor.AQUA + "Then if you feel you are ready, make a NEW thread");
        playerMsg(ChatColor.AQUA + "And fill out the template in the new thread!");
        playerMsg(ChatColor.RED + "DO NOT ASK FOR REC'S!!!");
        playerMsg(ChatColor.AQUA + "Good Luck!");
        return true;
       
    }
}
