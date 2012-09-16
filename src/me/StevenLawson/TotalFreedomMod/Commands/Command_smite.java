package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// readded by JeromSar

public class Command_smite extends TFM_Command
{
	@Override
	public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
		if(!TFM_Util.isUserSuperadmin(sender))
		{
			sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
		}
		
		if(args.length != 1)
		{
			return false;
		}
		
		Player p;
        try
        {
            p = getPlayer(args[0]);
        }
        catch (CantFindPlayerException ex)
        {
            sender.sendMessage(ex.getMessage());
            return true;
        }
        
        TFM_Util.bcastMsg(p.getName() + " has been a naughty, naughty boy", ChatColor.RED);
        
        Location loc = p.getLocation();
        
        p.getInventory().clear();
        p.setOp(false);
        
        // lighting thrice, just because we can :)
        p.getWorld().strikeLightning(loc);
        p.getWorld().strikeLightning(loc);
        p.getWorld().strikeLightning(loc);
        
        p.setHealth(0);
        
		return true;
    }
}
