package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Particle Cleanup", usage = "/<command>", aliases = "cp")
public class Command_clearparticles extends TFM_Command 
{        
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        {    
        server.dispatchCommand(sender, "wildcard gcmd ? pp clear");
        }
        TFM_Util.bcastMsg(sender.getName() + " - Cleared all particle effects from all players", ChatColor.RED);
        return true;
    }
}
