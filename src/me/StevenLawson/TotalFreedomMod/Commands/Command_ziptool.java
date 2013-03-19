package me.StevenLawson.TotalFreedomMod.Commands;

import java.io.File;
import java.io.IOException;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE, block_host_console = true)
public class Command_ziptool extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length <= 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("zip"))
        {
            File directory = new File("./" + args[1]);

            if (!directory.isDirectory())
            {
                playerMsg(directory.getPath() + " is not a directory.");
                return true;
            }

            File output = new File(directory.getParent() + "/" + directory.getName() + ".zip");

            playerMsg("Zipping '" + directory.getPath() + "' to '" + output.getPath() + "'.");

            try
            {
                TFM_Util.zip(directory, output, true, sender);
            }
            catch (IOException ex)
            {
                TFM_Log.severe(ex);
            }

            playerMsg("Zip finished.");
        }
        else if (args[0].equalsIgnoreCase("unzip"))
        {
            File output = new File(args[1]);

            if (!output.exists() || !output.isFile())
            {
                playerMsg(output.getPath() + " is not a file.");
                return true;
            }

            playerMsg("Unzipping '" + output.getPath() + "'.");

            try
            {
                TFM_Util.unzip(output, output.getParentFile());
            }
            catch (IOException ex)
            {
                TFM_Log.severe(ex);
            }

            playerMsg("Unzip finished.");
        }
        else
        {
            return false;
        }

        return true;
    }
}
