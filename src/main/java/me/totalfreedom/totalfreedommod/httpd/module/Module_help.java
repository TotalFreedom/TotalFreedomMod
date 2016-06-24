package me.totalfreedom.totalfreedommod.httpd.module;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.command.FreedomCommand;
import static me.totalfreedom.totalfreedommod.httpd.HTMLGenerationTools.heading;
import static me.totalfreedom.totalfreedommod.httpd.HTMLGenerationTools.paragraph;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import net.pravian.aero.command.CommandReflection;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;

public class Module_help extends HTTPDModule
{

    public Module_help(TotalFreedomMod plugin, NanoHTTPD.HTTPSession session)
    {
        super(plugin, session);
    }

    @Override
    public String getBody()
    {
        final CommandMap map = CommandReflection.getCommandMap();
        if (map == null || !(map instanceof SimpleCommandMap))
        {
            return paragraph("Error loading commands.");
        }

        final StringBuilder responseBody = new StringBuilder()
                .append(heading("Command Help", 1))
                .append(paragraph(
                                "This page is an automatically generated listing of all plugin commands that are currently live on the server. "
                                + "Please note that it does not include vanilla server commands."));

        final Collection<Command> knownCommands = ((SimpleCommandMap) map).getCommands();
        final Map<String, List<Command>> commandsByPlugin = new HashMap<>();

        for (Command command : knownCommands)
        {
            String pluginName = "Bukkit";
            if (command instanceof PluginIdentifiableCommand)
            {
                pluginName = ((PluginIdentifiableCommand) command).getPlugin().getName();
            }

            List<Command> pluginCommands = commandsByPlugin.get(pluginName);
            if (pluginCommands == null)
            {
                pluginCommands = Lists.newArrayList();
                commandsByPlugin.put(pluginName, pluginCommands);
            }

            pluginCommands.add(command);
        }

        final Iterator<Map.Entry<String, List<Command>>> it = commandsByPlugin.entrySet().iterator();
        while (it.hasNext())
        {
            final Map.Entry<String, List<Command>> entry = it.next();
            final String pluginName = entry.getKey();
            final List<Command> commands = entry.getValue();

            Collections.sort(commands, new CommandComparator());

            responseBody.append(heading(pluginName, 2)).append("<ul>\r\n");

            Displayable lastTfmCommandLevel = null;
            for (Command command : commands)
            {
                if (!TotalFreedomMod.pluginName.equals(pluginName))
                {
                    responseBody.append(buildDescription(command));
                    continue;
                }

                Displayable tfmCommandLevel = FreedomCommand.getFrom(command).getPerms().level();
                if (lastTfmCommandLevel == null || lastTfmCommandLevel != tfmCommandLevel)
                {
                    responseBody.append("</ul>\r\n").append(heading(tfmCommandLevel.getName(), 3)).append("<ul>\r\n");
                }
                lastTfmCommandLevel = tfmCommandLevel;
                responseBody.append(buildDescription(command));
            }

            responseBody.append("</ul>\r\n");
        }

        return responseBody.toString();
    }

    private static String buildDescription(Command command)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "<li><span class=\"commandName\">{$CMD_NAME}</span> - Usage: <span class=\"commandUsage\">{$CMD_USAGE}</span>"
                .replace("{$CMD_NAME}", escapeHtml4(command.getName().trim()))
                .replace("{$CMD_USAGE}", escapeHtml4(command.getUsage().trim())));

        if (!command.getAliases().isEmpty())
        {
            sb.append(
                    " - Aliases: <span class=\"commandAliases\">{$CMD_ALIASES}</span>"
                    .replace("{$CMD_ALIASES}", escapeHtml4(StringUtils.join(command.getAliases(), ", "))));
        }

        sb.append(
                "<br><span class=\"commandDescription\">{$CMD_DESC}</span></li>\r\n"
                .replace("{$CMD_DESC}", escapeHtml4(command.getDescription().trim())));

        return sb.toString();
    }

    @Override
    public String getTitle()
    {
        return "TotalFreedomMod :: Command Help";
    }

    @Override
    public String getStyle()
    {
        return ".commandName{font-weight:bold;}.commandDescription{padding-left:15px;}li{margin:.15em;padding:.15em;}";
    }

    public static class CommandComparator implements Comparator<Command>
    {

        @Override
        public int compare(Command a, Command b)
        {
            FreedomCommand ca = FreedomCommand.getFrom(a);
            FreedomCommand cb = FreedomCommand.getFrom(b);

            if (ca == null
                    || cb == null
                    || ca.getPerms() == null
                    || cb.getPerms() == null)
            {
                return a.getName().compareTo(b.getName());
            }

            return ca.getPerms().level().getName().compareTo(cb.getPerms().level().getName());
        }
    }

}
