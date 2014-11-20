package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.StevenLawson.TotalFreedomMod.Commands.AdminLevel;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandLoader;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandLoader.TFM_DynamicCommand;
import static me.StevenLawson.TotalFreedomMod.HTTPD.HTMLGenerationTools.heading;
import static me.StevenLawson.TotalFreedomMod.HTTPD.HTMLGenerationTools.paragraph;
import static net.minecraft.util.org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;

public class Module_help extends TFM_HTTPD_Module
{
    public Module_help(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    @Override
    public String getBody()
    {
        StringBuilder responseBody = new StringBuilder();

        CommandMap commandMap;
        HashMap<String, Command> knownCommands;
        if ((commandMap = TFM_CommandLoader.getCommandMap()) == null
                || (knownCommands = TFM_CommandLoader.getKnownCommands(commandMap)) == null)
        {
            return paragraph("Error loading commands.");
        }

        responseBody
                .append(heading("Command Help", 1))
                .append(paragraph(
                                "This page is an automatically generated listing of all plugin commands that are currently live on the server. "
                                + "Please note that it does not include vanilla server commands."));

        final Map<String, List<Command>> commandsByPlugin = new HashMap<String, List<Command>>();

        final Iterator<Map.Entry<String, Command>> itKnownCommands = knownCommands.entrySet().iterator();
        while (itKnownCommands.hasNext())
        {
            final Map.Entry<String, Command> entry = itKnownCommands.next();
            final String name = entry.getKey();
            final Command command = entry.getValue();
            if (name.equalsIgnoreCase(command.getName()))
            {
                String pluginName = "Bukkit Default";
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
                    if (a instanceof TFM_DynamicCommand && b instanceof TFM_DynamicCommand)
                    {
                        String aName = ((TFM_DynamicCommand) a).getCommandInfo().getLevel().name() + a.getName();
                        String bName = ((TFM_DynamicCommand) b).getCommandInfo().getLevel().name() + b.getName();
                        return aName.compareTo(bName);
                    }
                    return a.getName().compareTo(b.getName());
                }
            });

            responseBody.append(heading(pluginName, 2)).append("<ul>\r\n");

            AdminLevel lastTfmCommandLevel = null;
            for (Command command : commands)
            {
                if ("TotalFreedomMod".equals(pluginName))
                {
                    AdminLevel tfmCommandLevel = ((TFM_DynamicCommand) command).getCommandInfo().getLevel();
                    if (lastTfmCommandLevel == null || lastTfmCommandLevel != tfmCommandLevel)
                    {
                        responseBody.append("</ul>\r\n").append(heading(tfmCommandLevel.getFriendlyName(), 3)).append("<ul>\r\n");
                    }
                    lastTfmCommandLevel = tfmCommandLevel;
                }
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
//    @Override
//    public String getScript()
//    {
//        return "$(document).ready(function(){console.log(\"Ready\");});";
//    }
}
