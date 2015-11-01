package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Bridge.TFM_DisguiserBridge;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Lists all disguised players", usage = "/<command> [-a]")
public class Command_ulist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            TFM_DisguiserBridge.listDisguisedPlayers(sender);
        }
        if (args.length == 1)
        {
            if(args[0].equalsIgnoreCase("-a"))
            {
                sender.sendMessage(ChatColor.RED + "Not implemented yet.");
                return true;
            }
        }
        return true;
    }
    
}
