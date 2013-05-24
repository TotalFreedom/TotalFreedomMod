package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Command_dtoggle extends TFM_Command
{
	@Override
	public boolean run(CommandSender sender, Player sender_p, Command cmd,String commandLabel, String[] args, boolean senderIsConsole)
	{   
    	boolean toggled = false;
    	boolean enabled = true;
    	for(Plugin p : TotalFreedomMod.server.getPluginManager().getPlugins()) {
    		if(p.getName().equalsIgnoreCase("disguisecraft")) {
    			if(p.isEnabled()) {
    				p.getPluginLoader().disablePlugin(p);
    				enabled = false;
    			} else {
    				p.getPluginLoader().enablePlugin(p);
    				enabled = true;
    			}
    			toggled = true;
    		}
    	}
    	if(toggled) {
    		if(!enabled) {
    			TFM_Util.adminAction(sender.getName(), "Disabling DisguiseCraft", true);
    		} else {
    			TFM_Util.adminAction(sender.getName(), "Enabling DisguiseCraft", false);
    		}
    	}
//Credit to disaster, this is based off of disaster's /dtoggle.
    	
		return enabled;
        
		
	}
}
	   
	



