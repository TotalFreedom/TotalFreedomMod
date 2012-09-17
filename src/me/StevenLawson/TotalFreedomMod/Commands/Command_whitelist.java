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
		}

		
		return true;
    }
}
