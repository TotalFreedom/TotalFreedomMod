package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Get social media links.", usage = "/<command>", aliases = "link")
public class Command_links extends FreedomCommand
{
    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("social_links");
        Map<String,Object> values = section.getValues(false);

        List<String> lines = new ArrayList<>();

        for (String key : values.keySet())
        {
            if (!(values.get(key) instanceof String))
            {
                continue;
            }

            String link = (String) values.get(key);

            lines.add(ChatColor.GOLD + "- " + key + ": " + ChatColor.AQUA + link);
        }
        if (lines.size() == 0)
        {
            lines.add(ChatColor.GOLD + "- There are no links currently added in the config.");
        }

        lines.add(ChatColor.AQUA + "TotalFreedom Social Media Links:");
        sender.sendMessage(lines.toArray(new String[0]));

        return true;
    }
}
