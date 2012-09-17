package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_whitelist extends TFM_Command
{
	@Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
		if(args.length < 1)
		{
			return false;
		}
		
		if(!sender.isOp())
		{
			sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
			return true;
		}
		
		// list
		if(args[0].equalsIgnoreCase("list"))
		{
			String players = TFM_Util.arrayToString(server.getWhitelistedPlayers());
			
			TFM_Util.playerMsg(sender, "Whitelisted players:");
			TFM_Util.playerMsg(sender, players);
			return true;
		}
		
		// count
		if(args[0].equalsIgnoreCase("count"))
		{
            int onlineWPs = 0;
            int offlineWPs = 0;
            int totalWPs = 0;

            for (OfflinePlayer p : server.getWhitelistedPlayers())
            {
                if (p.isOnline())
                {
                    onlineWPs++;
                }
                else
                {
                    offlineWPs++;
                }
                totalWPs++;
            }

            sender.sendMessage(ChatColor.GRAY + "Online whitelisted players: " + onlineWPs);
            sender.sendMessage(ChatColor.GRAY + "Offline whitelisted players: " + offlineWPs);
            sender.sendMessage(ChatColor.GRAY + "Total whitelisted players: " + totalWPs);

            return true;
		}
		
		// all commands past this line are superadmin-only
		if(!(senderIsConsole || TFM_Util.isUserSuperadmin(sender)))
		{
			sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
			return true;
		}
		
		// on
		if(args[0].equalsIgnoreCase("on"))
		{
			TFM_Util.adminAction(sender.getName(), "Turning the whitelist on", false);
			server.setWhitelist(true);
			return true;
		}
		
		// off
		if(args[0].equalsIgnoreCase("off"))
		{
			TFM_Util.adminAction(sender.getName(), "Turning the whitelist off", false);
			server.setWhitelist(false);
			return true;
		}
		
		// add
		if(args[0].equalsIgnoreCase("add"))
		{
			if(args.length < 2)
			{
				return false;
			}
			
			OfflinePlayer p;
			try
			{
				p = getPlayer(args[0]);
			}
			catch(CantFindPlayerException ex)
			{
				if(!senderIsConsole)
				{
					sender.sendMessage(ex.getMessage());
					sender.sendMessage(ChatColor.YELLOW + "You don't have permissions to whitelist offline players");
					return true;
				}
				else
				{
					p = server.getOfflinePlayer(args[0]);
				}
			}
			TFM_Util.adminAction(sender.getName(), "Adding " + p.getName() + " to the whitelist", false);
			p.setWhitelisted(true);
			return true;
		}
		
		// remove
		if(args[0].equalsIgnoreCase("remove"))
		{
			if(args.length < 2)
			{
				return false;
			}
			
			OfflinePlayer p;
			try
			{
				p = getPlayer(args[0]);
			}
			catch(CantFindPlayerException ex)
			{
				p = server.getOfflinePlayer(args[0]);
			}
			
			if(p.isWhitelisted())
			{
				TFM_Util.adminAction(sender.getName(), "Removing " + p.getName() + "from the whitelist", false);
				p.setWhitelisted(false);
				return true;
			}
			else
			{
				TFM_Util.playerMsg(sender, "That player is not whitelisted");
				return true;
			}
			
		}
		
		// addall
		if(args[0].equalsIgnoreCase("addall"))
		{
			TFM_Util.adminAction(sender.getName(), "Adding all online players to the whitelist", false);
			int counter = 0;
			for(Player p : server.getOnlinePlayers())
			{
				if(!p.isWhitelisted())
				{
					p.setWhitelisted(true);
					counter++;
				}
			}
			
			TFM_Util.playerMsg(sender, "Whitelisted " + counter + " players.");
			return true;
		}
		
		// all commands past this line are console/telnet only
		if(!senderIsConsole)
		{
			sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
			return true;
		}
		
		//purge
		if(args[0].equalsIgnoreCase("purge"))
		{
			TFM_Util.adminAction(sender.getName(), "Removing all players from the whitelist", true);
			int counter = 0;
			for(OfflinePlayer p : server.getWhitelistedPlayers())
			{
				p.setWhitelisted(false);
				counter++;
			}
			TFM_Util.playerMsg(sender, "Removed " + counter + " players from the whitelist");
			
			return true;
		}
		
		// none of the commands were executed
		return false;
    }
}
