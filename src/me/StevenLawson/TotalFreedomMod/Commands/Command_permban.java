package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_permban extends TFM_Command
{
	@Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
		if(!sender.isOp())
		{
			sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
			return true;
		}
		
		if(args.length != 1)
		{
			return false;
		}
		
		if(args[0].equalsIgnoreCase("list"))
		{
			TFM_Util.playerMsg(sender, "Permanently banned players:");
            int count = 0;
            for (String pbp : TotalFreedomMod.permbanned_players)
            {
                TFM_Util.playerMsg(sender, "- " + pbp);
                count++;
            }
            if (count == 0)
            {
                TFM_Util.playerMsg(sender, "- none");
            }
            else
            {
            	TFM_Util.playerMsg(sender, "Total: " + count);
            }
            
            count = 0;
            TFM_Util.playerMsg(sender, "Permanently banned IPs:");
            for (String pbp : TotalFreedomMod.permbanned_ips)
            {
                TFM_Util.playerMsg(sender, "- " + pbp);
                count++;
            }
            if (count == 0)
            {
                TFM_Util.playerMsg(sender, "- none");
            }
            else
            {
            	TFM_Util.playerMsg(sender, "Total: " + count);
            }
            
            return true;
		}
		
		if(!senderIsConsole)
		{
			sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
			return true;
		}
		
		if(args[0].equalsIgnoreCase("reload"))
		{
			plugin.loadPermbanConfig();
			return true;
		}
		
		// no command executed
		return false;
    }
}
