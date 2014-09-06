package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Show all commands for all server plugins.", usage = "/<command>")
public class Command_cmdlist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        List<String> commands = new ArrayList<String>();

        for (Plugin targetPlugin : server.getPluginManager().getPlugins())
        {
            try
            {
                PluginDescriptionFile desc = targetPlugin.getDescription();
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

        sender.sendMessage(StringUtils.join(commands, ","));

        return true;
    }
}
