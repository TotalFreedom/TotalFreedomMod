package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;

import static me.StevenLawson.TotalFreedomMod.HTTPD.HTMLGenerationTools.*;
import static org.apache.commons.lang3.StringEscapeUtils.*;
import org.apache.commons.lang3.StringUtils;

public class Module_help extends TFM_HTTPD_Module
{
    public Module_help(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files)
    {
        super(uri, method, headers, params, files);
    }

    @Override
    public String getBody()
    {
        StringBuilder responseBody = new StringBuilder();

        CommandMap commandMap;
        HashMap<String, Command> knownCommands;
        if ((commandMap = TFM_CommandLoader.getInstance().getCommandMap()) == null
                || (knownCommands = TFM_CommandLoader.getInstance().getKnownCommands(commandMap)) == null)
        {
            return paragraph("Error loading commands.");
        }

        responseBody.append(heading("Command Help Index", 1));

        final Map<String, List<Command>> commandsByPlugin = new HashMap<String, List<Command>>();

        final Iterator<Map.Entry<String, Command>> itKnownCommands = knownCommands.entrySet().iterator();
        while (itKnownCommands.hasNext())
        {
            final Map.Entry<String, Command> entry = itKnownCommands.next();
            final String name = entry.getKey();
            final Command command = entry.getValue();
            if (name.equalsIgnoreCase(command.getName()))
            {
                String pluginName = "Bukkit";
                if (command instanceof PluginIdentifiableCommand)
                {
                    pluginName = ((PluginIdentifiableCommand) command).getPlugin().getName();
                }
                List<Command> pluginCommands = commandsByPlugin.get(pluginName);
                if (pluginCommands == null)
                {
                    commandsByPlugin.put(pluginName, pluginCommands = new ArrayList<Command>());
                }
                pluginCommands.add(command);
            }
        }

        final Iterator<Map.Entry<String, List<Command>>> itCommandsByPlugin = commandsByPlugin.entrySet().iterator();
        while (itCommandsByPlugin.hasNext())
        {
            final Map.Entry<String, List<Command>> entry = itCommandsByPlugin.next();
            final String pluginName = entry.getKey();
            final List<Command> commands = entry.getValue();

            Collections.sort(commands, new Comparator<Command>()
            {
                @Override
                public int compare(Command a, Command b)
                {
                    return a.getName().compareTo(b.getName());
                }
            });

            responseBody.append(heading(pluginName, 2)).append("<ul>\r\n");

            for (Command command : commands)
            {
                responseBody
                        .append("<li><div><span class=\"commandName\">")
                        .append(escapeHtml4(command.getName()))
                        .append("</span> - Usage: <span class=\"commandUsage\">")
                        .append(escapeHtml4(command.getUsage().replace("<command>", command.getName()).trim()))
                        .append("</span>");

                if (!command.getAliases().isEmpty())
                {
                    responseBody.append(" - Aliases: <span class=\"commandAliases\">")
                            .append(escapeHtml4(StringUtils.join(command.getAliases(), ", ")))
                            .append("</span>");
                }

                responseBody.append("<br /><span class=\"commandDescription\">")
                        .append(escapeHtml4(command.getDescription()))
                        .append("</span></div></li>\r\n");
            }

            responseBody.append("</ul>\r\n");
        }

        return responseBody.toString();
    }

    @Override
    public String getTitle()
    {
        return "TotalFreedomMod :: WebHelp";
    }

    @Override
    public String getStyle()
    {
        return ".commandName{font-weight:bold;}.commandDescription{padding-left:15px;}li{margin:.15em;padding:.15em;}";
    }
}
