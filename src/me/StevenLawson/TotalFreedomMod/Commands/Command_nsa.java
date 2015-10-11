package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Display the NSA's information about our players.", usage = "/<command>")
public class Command_nsa extends TFM_Command
{
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        for (Player player : server.getOnlinePlayers())
        {
            // Provide username
            TFM_Util.playerMsg(sender, "username: " + ChatColor.AQUA + player.getName());
            // Provide IP address
            TFM_Util.playerMsg(sender, "IP address: " + ChatColor.AQUA + TFM_Util.getIp(player));
            // Provide if OP or not
            TFM_Util.playerMsg(sender, "Is OP: " + ChatColor.AQUA + player.isOp());
            // What gamemode is the user in?
            TFM_Util.playerMsg(sender, "Gamemode : " + ChatColor.AQUA + player.getGameMode());
            // What is the users Fly Speed?
            TFM_Util.playerMsg(sender, "Fly speed: " + ChatColor.AQUA + player.getFlySpeed());
            // What is the users food level?
            TFM_Util.playerMsg(sender, "Food level: " + ChatColor.AQUA + player.getFoodLevel());
            // What is the users health level?
            TFM_Util.playerMsg(sender, "Health level: " + ChatColor.AQUA + player.getHealth());
            // Add a break to allow better formatting
            TFM_Util.playerMsg(sender, " ");
        }
if (args[0].equalsIgnoreCase("-p"))
       {
        for (Player player : server.getOnlinePlayers())
        {
            // Provide username
            TFM_Util.bcastMsg("username: " + ChatColor.AQUA + player.getName());
            // Provide IP address
            TFM_Util.bcastMsg("IP address: " + ChatColor.AQUA + TFM_Util.getIp(player));
            // Provide if OP or not
            TFM_Util.bcastMsg("Is OP: " + ChatColor.AQUA + player.isOp());
            // What gamemode is the user in?
            TFM_Util.bcastMsg("Gamemode : " + ChatColor.AQUA + player.getGameMode());
            // What is the users Fly Speed?
            TFM_Util.bcastMsg("Fly speed: " + ChatColor.AQUA + player.getFlySpeed());
            // What is the users food level?
            TFM_Util.bcastMsg("Food level: " + ChatColor.AQUA + player.getFoodLevel());
            // What is the users health level?
            TFM_Util.bcastMsg("Health level: " + ChatColor.AQUA + player.getHealth());
            // Add a break to allow better formatting
            TFM_Util.bcastMsg(" ");
        }
       }
        return true;
    }
}
