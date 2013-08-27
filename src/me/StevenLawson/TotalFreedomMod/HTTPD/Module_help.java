package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Module_help extends TFM_HTTPD_Module
{
    public Module_help(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files)
    {
        super(uri, method, headers, params, files);
    }

    @Override
    public String getBody()
    {
        final StringBuilder body = new StringBuilder();

        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : plugins)
        {
            Map<String, Map<String, Object>> commands = plugin.getDescription().getCommands();
            if (commands != null)
            {
                Iterator<Map.Entry<String, Map<String, Object>>> it1 = commands.entrySet().iterator();
                while (it1.hasNext())
                {
                    Map.Entry<String, Map<String, Object>> next1 = it1.next();
                    String key1 = next1.getKey();
                    Map<String, Object> value1 = next1.getValue();

                    Iterator<Map.Entry<String, Object>> it2 = value1.entrySet().iterator();
                    while (it2.hasNext())
                    {
                        Map.Entry<String, Object> next2 = it2.next();
                        String key2 = next2.getKey();
                        Object value2 = next2.getValue();

                        body
                                .append("<p>")
                                .append(StringEscapeUtils.escapeHtml(key1))
                                .append(".")
                                .append(StringEscapeUtils.escapeHtml(key2))
                                .append(" = ")
                                .append(StringEscapeUtils.escapeHtml(value2.toString()))
                                .append("</p>\r\n");
                    }
                }
            }
        }

        return body.toString();
    }

    @Override
    public String getTitle()
    {
        return "Module_help";
    }
}
