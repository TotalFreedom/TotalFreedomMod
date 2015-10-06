package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

import org.bukkit.ChatColor;
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
    	boolean toggled = false;
    	boolean enabled = true;
        
    	for (Plugin playerparticles : TotalFreedomMod.server.getPluginManager().getPlugins()) {
    		if (playerparticles.getName().equalsIgnoreCase("playerparticles")) {
    			if (playerparticles.isEnabled()) {
    				playerparticles.getPluginLoader().disablePlugin(playerparticles);
    				enabled = false;
    			} else {
    				playerparticles.getPluginLoader().enablePlugin(playerparticles);
    				enabled = true;
    			}
    			toggled = true;
    		}
    	}
    	if (toggled) {
    		if (!enabled) {
                TFM_Util.adminAction(sender.getName(), "Disabling PlayerParticles", true);
    		} else {
    			TFM_Util.adminAction(sender.getName(), "Enabling PlayerParticles", false);
    		}
    	}
        return true;
    }
}
