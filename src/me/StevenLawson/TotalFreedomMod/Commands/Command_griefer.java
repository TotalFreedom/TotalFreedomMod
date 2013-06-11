package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Report a griefer", usage = "/<command> [message]", aliases = "report")
public class Command_griefer extends TFM_Command
{
   @Override
   public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
   {
      if (args.length == 0)
      {
        TFM_Util.adminbcastMsg(sender.getName() + " is getting griefed!", ChatColor.RED);
        return true;
      }
		
      String message = StringUtils.join(args, " ");
		
      TFM_Util.adminbcastMsg(String.format("[GrieferReport:%s] %s", sender.getName(), message), ChatColor.RED);
		
      return true;
      //On-request: Credit to mrnintendofan for the idea!
   }
	
}
