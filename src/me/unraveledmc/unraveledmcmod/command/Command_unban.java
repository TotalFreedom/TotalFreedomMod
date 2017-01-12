package me.unraveledmc.unraveledmcmod.command;

import java.util.ArrayList;
import java.util.List;
import me.unraveledmc.unraveledmcmod.banning.Ban;
import me.unraveledmc.unraveledmcmod.player.PlayerData;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Unbans a player", usage = "/<command> <username>")
public class Command_unban extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
    	if (args.length == 1)
    	{
	        String username;
	        final List<String> ips = new ArrayList<>();
	        final PlayerData entry = plugin.pl.getData(args[0]);
	
	        if (entry == null)
	        {
	            msg("Can't find that user.");
	            return true;
	        }
	
	        username = entry.getUsername();
	        ips.addAll(entry.getIps());
	        
	        FUtil.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
	        plugin.bm.removeBan(plugin.bm.getByUsername(username));
	
	        for (String ip : ips)
	        {
	            Ban ban = plugin.bm.getByIp(ip);
	            if (ban != null)
	            {
	                plugin.bm.removeBan(ban);
	            }
	            ban = plugin.bm.getByIp(FUtil.getFuzzyIp(ip));
	            if (ban != null)
	            {
	                plugin.bm.removeBan(ban);
	            }
	        }
	        return true;
    	}

        return false;
    }
}
