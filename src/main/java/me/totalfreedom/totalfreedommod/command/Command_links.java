package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
        YamlConfiguration config = plugin.config.configuration;
        ConfigurationSection section = config.getConfigurationSection("social_links");
        if (section != null)
        {
            Map<String, Object> values = section.getValues(false);

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

            msg("Social Media Links:", ChatColor.AQUA);
            sender.sendMessage(lines.toArray(new String[0]));
            return true;
        }
        else
        {
            msg("There are no links added in the configuration file.", ChatColor.RED);
        }
        return true;
    }
}
