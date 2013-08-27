package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.util.Iterator;
import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.*;

public class HTMLGenerationTools
{
    private HTMLGenerationTools()
    {
        throw new AssertionError();
    }

    public static String paragraph(String data)
    {
        return "<p>" + escapeHtml4(data) + "</p>\r\n";
    }

    public static String mapToHTMLList(Map<String, String> map)
    {
        StringBuilder output = new StringBuilder();

        output.append("<ul>\r\n");

        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, String> entry = it.next();
            output.append("<li>").append(escapeHtml4(entry.getKey() + " = " + entry.getValue())).append("</li>\r\n");
        }

        output.append("</ul>\r\n");

        return output.toString();
    }
}
