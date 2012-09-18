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
		
		TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);
		
		if(!playerdata.isHalted())
		{
			TFM_Util.adminAction(sender.getName(), "Halting " + p.getName(), true);
			
			p.setOp(false);
			p.setGameMode(GameMode.SURVIVAL);
			p.setFlying(false);
			p.setDisplayName(p.getName());
			p.closeInventory();
			p.setTotalExperience(0);
			
			playerdata.stopOrbiting();
			playerdata.setFrozen(true);
			playerdata.setMuted(true);
			
			TFM_Util.playerMsg(p, "You have been halted, don't move!");
			return true;
		}
		else
		{
			TFM_Util.adminAction(sender.getName(), "Unhalting " + p.getName(), true);
			
			p.setOp(true);
			p.setGameMode(GameMode.CREATIVE);
			playerdata.setFrozen(false);
			playerdata.setMuted(false);
			
			TFM_Util.playerMsg(p, "You are no longer halted.");
			return true;
		}
		
	}
}
