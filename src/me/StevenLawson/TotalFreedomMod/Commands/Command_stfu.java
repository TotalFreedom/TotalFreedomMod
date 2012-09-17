package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//This command was coded initially by JeromSar

public class Command_stfu extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (!(senderIsConsole || TFM_Util.isUserSuperadmin(sender)))
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            TFM_Util.playerMsg(sender, "Muted players:");
            TFM_UserInfo info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = TFM_UserInfo.getPlayerData(mp);
                if (info.isMuted())
                {
                    TFM_Util.playerMsg(sender, "- " + mp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                TFM_Util.playerMsg(sender, "- none");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("purge"))
        {
            TFM_Util.adminAction(sender.getName(), "Unmuting all players.", true);
            TFM_UserInfo info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = TFM_UserInfo.getPlayerData(mp);
                if (info.isMuted())
                {
                    info.setMuted(false);
                    count++;
                }
            }
            TFM_Util.playerMsg(sender, "Unmuted " + count + " players.");
            return true;
        }
        
        if(args[0].equalsIgnoreCase("all"))
        {
        	TFM_Util.adminAction(sender.getName(), "Muting all non-Superadmins", true);
        	
        	TFM_UserInfo playerdata;
        	int counter = 0;
        	for(Player p : server.getOnlinePlayers())
        	{
        		if(!TFM_Util.isUserSuperadmin(p))
        		{
            		playerdata = TFM_UserInfo.getPlayerData(p);
            		playerdata.setMuted(true);
            		counter++;
        		}
        	}
        	
        	TFM_Util.playerMsg(sender, "Muted " + counter +  " players.");
        	
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

        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);
        if (playerdata.isMuted())
        {
            TFM_Util.adminAction(sender.getName(), "Unmuting " + p.getName(), true);
            playerdata.setMuted(false);
            TFM_Util.playerMsg(sender, "Unmuted " + p.getName());
            return true;
        }
        else
        {
            TFM_Util.adminAction(sender.getName(), "Muting " + p.getName(), true);
            playerdata.setMuted(true);
            TFM_Util.playerMsg(sender, "Muted " + p.getName());
        }

        return true;
    }
}
