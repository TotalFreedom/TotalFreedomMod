package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class Command_cmdlist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        List<String> commands = new ArrayList<String>();
        
        for (Plugin p : plugin.getServer().getPluginManager().getPlugins())
        {
            try
            {
                PluginDescriptionFile desc = p.getDescription();
                Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) desc.getCommands();

                if (map != null)
                {
                    for (Entry<String, Map<String, Object>> entry : map.entrySet())
                    {
                        String command_name = (String) entry.getKey();
                        commands.add(command_name);
                    }
                }
            }
            catch (Throwable ex)
            {
            }
        }
        
        Collections.sort(commands);
        
        sender.sendMessage(TFM_Util.implodeStringList(",", commands));

        return true;
    }
}
