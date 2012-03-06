package me.StevenLawson.TotalFreedomMod.Commands;

import java.io.File;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_wipeflatlands extends TFM_Command
{
    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }
        
        TFM_Util.bcastMsg("Flatlands is being wiped.", ChatColor.RED);
        
        server.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                World flatlands = server.getWorld("flatlands");
                
                if (flatlands != null)
                {
                    for (Player p : flatlands.getPlayers())
                    {
                        p.teleport(server.getWorlds().get(0).getSpawnLocation());
                    }

                    if (server.unloadWorld(flatlands, false))
                    {
                        File flatlands_folder = new File("./flatlands");

                        if (flatlands_folder.exists())
                        {
                            TFM_Util.deleteFolder(flatlands_folder);
                        }

                        if (flatlands_folder.exists())
                        {
                            sender.sendMessage("Old Flatlands folder could not be deleted.");
                        }
                        else
                        {
                            TFM_Util.generateFlatlands();
                        }
                    }
                    else
                    {
                        sender.sendMessage("Flatlands could not be unloaded.");
                    }
                }
                else
                {
                    sender.sendMessage("Flatlands is not loaded.");
                }
            }
        });
        
        return true;
    }
}
