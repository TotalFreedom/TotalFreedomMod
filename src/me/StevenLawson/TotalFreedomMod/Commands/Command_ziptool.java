package me.StevenLawson.TotalFreedomMod.Commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_ziptool extends TFM_Command
{
	@Override
	public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
	{
		if (!senderIsConsole || sender.getName().equalsIgnoreCase("remotebukkit"))
		{
			sender.sendMessage(ChatColor.GRAY + "This command may only be used from the Telnet or BukkitHttpd console.");
			return true;
		}
		
		if (args.length <= 1)
		{
			return false;
		}
		
		if (args[0].equalsIgnoreCase("zip"))
		{
			File directory = new File("./" + args[1]);
			
			if (!directory.isDirectory())
			{
				sender.sendMessage(directory.getPath() + " is not a directory.");
				return true;
			}
			
			File output = new File(directory.getParent() + "/" + directory.getName() + ".zip");
			
			sender.sendMessage("Zipping '" + directory.getPath() + "' to '" + output.getPath() + "'.");
			
			try
			{
				TFM_Util.zip(directory, output, true, sender);
			}
			catch (IOException ex)
			{
				log.log(Level.SEVERE, null, ex);
			}
            
            sender.sendMessage("Zip finished.");
		}
		else if (args[0].equalsIgnoreCase("unzip"))
		{
            File output = new File(args[1]);
            
            if (!output.exists() || !output.isFile())
            {
                sender.sendMessage(output.getPath() + " is not a file.");
                return true;
            }
            
			sender.sendMessage("Unzipping '" + output.getPath() + "'.");
            
			try
			{
				TFM_Util.unzip(output, output.getParentFile());
			}
			catch (IOException ex)
			{
				log.log(Level.SEVERE, null, ex);
			}
            
            sender.sendMessage("Unzip finished.");
		}
		else
		{
			return false;
		}

		return true;
	}
}
