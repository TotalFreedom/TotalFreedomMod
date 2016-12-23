package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Toggles chat for the user", usage = "/<command>", aliases = "ct")
public class Command_chattoggle extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            TFM_ServerInterface.sendTitle(sender_p, ChatColor.WHITE + "Your chat has been turned: " + ChatColor.GREEN + "ON", 2, 2, 2);
            for(;;)
            {
                sender.sendMessage(" ");
            }
        } else
        {            
            TFM_ServerInterface.sendTitle(sender_p, ChatColor.WHITE + "" + "Your chat has been turned: " + ChatColor.RED + "OFF", 2, 2, 2);
        }
        return true;
    }
}
