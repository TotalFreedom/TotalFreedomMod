package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Particle Cleanup", usage = "/<command>", aliases = "cc")
public class Command_clearparticles extends TFM_Command 
{        
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        for (int i = 0; i <= 80; i++)
        {    
            TFM_Util.bcastMsg("");
        }
        TFM_Util.adminAction(sender.getName(), "Clearing all particles ", true);
        return true;
    }
}
