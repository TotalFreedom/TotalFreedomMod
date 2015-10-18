package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Turns on or off the PlayerParticles plugin", usage = "/<command>")
public class Command_pptoggle extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {	
    	for (Plugin p : TotalFreedomMod.server.getPluginManager().getPlugins()) {
    	    if (p.getName().equalsIgnoreCase("PlayerParticles")) {
    		    if (p.isEnabled()) {
                  p.getPluginLoader().disablePlugin(p);
    	          TFM_Util.adminAction(sender.getName(), "Disabling PlayerParticles", true);
    	            } 
    	        else 
    	            {
    		        p.getPluginLoader().enablePlugin(p);
    		        TFM_Util.adminAction(sender.getName(), "Enabled PlayerParticles", false);
    			    }
    		    }
            }
        return true;
    }
}
