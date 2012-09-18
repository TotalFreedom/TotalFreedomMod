package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_halt extends TFM_Command
{
	@Override
	public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
	{
		if(!(TFM_Util.isUserSuperadmin(sender) || senderIsConsole))
		{
			sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
			return true;
		}
		
		if(args.length != 1)
		{
			return false;
		}
		
		if(args[0].equalsIgnoreCase("all"))
		{
			TFM_Util.adminAction(sender.getName(), "Halting all non-Superadmins", true);
			int counter = 0;
			for(Player p : server.getOnlinePlayers())
			{
				if(!TFM_Util.isUserSuperadmin(p))
				{
					halt(p, sender);
					counter++;
				}
			}
			TFM_Util.playerMsg(sender, "Halted " + counter + " players.");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("purge"))
		{
			TFM_Util.adminAction(sender.getName(), "Unhalting all players", true);
			int counter = 0;
			for(Player p : server.getOnlinePlayers())
			{
				if(TFM_UserInfo.getPlayerData(p).isHalted())
				{
					unhalt(p, sender);
					counter++;
				}
			}
			TFM_Util.playerMsg(sender, "Unhalted " + counter + " players.");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("list"))
		{
			TFM_Util.playerMsg(sender, "Halted players:");
            TFM_UserInfo info;
            int count = 0;
            for (Player hp : server.getOnlinePlayers())
            {
                info = TFM_UserInfo.getPlayerData(hp);
                if (info.isHalted())
                {
                    TFM_Util.playerMsg(sender, "- " + hp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                TFM_Util.playerMsg(sender, "- none");
            }
            return true;
		}
		
		Player p;
		try
		{
			p = getPlayer(args[0]);
		}
		catch(CantFindPlayerException ex)
		{
			sender.sendMessage(ex.getMessage());
			return true;
			
		}
		
		if(!TFM_UserInfo.getPlayerData(p).isHalted())
		{
			TFM_Util.adminAction(sender.getName(), "Halting " + p.getName(), true);
			halt(p, sender);
			return true;
		}
		else
		{
			TFM_Util.adminAction(sender.getName(), "Unhalting " + p.getName(), true);
			
			unhalt(p, sender);
			return true;
		}
	}
	
	public void halt(Player p, CommandSender sender)
	{
		TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);
		
		p.setOp(false);
		p.setGameMode(GameMode.SURVIVAL);
		p.setFlying(false);
		p.setDisplayName(p.getName());
		p.closeInventory();
		p.setTotalExperience(0);
		
		playerdata.stopOrbiting();
		playerdata.setFrozen(true);
		playerdata.setMuted(true);
		playerdata.setHalted(true);
		
		TFM_Util.playerMsg(p, "You have been halted, don't move!");
	}
	
	public void unhalt(Player p, CommandSender sender)
	{
		TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);
		
		p.setOp(true);
		p.setGameMode(GameMode.CREATIVE);
		playerdata.setFrozen(false);
		playerdata.setMuted(false);
		playerdata.setHalted(false);
		TFM_Util.playerMsg(p, "You are no longer halted.");
	}
}
