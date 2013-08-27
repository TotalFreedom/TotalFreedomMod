package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.util.Map;
import org.apache.commons.lang.StringUtils;

import static me.StevenLawson.TotalFreedomMod.HTTPD.HTMLGenerationTools.*;

public class Module_dump extends TFM_HTTPD_Module
{
    public Module_dump(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files)
    {
        super(uri, method, headers, params, files);
    }

    @Override
    public String getBody()
    {
        StringBuilder responseBody = new StringBuilder();

        String[] args = StringUtils.split(uri, "/");

        responseBody
                .append(paragraph("URI: " + uri))
                .append(paragraph("args (Length: " + args.length + "): " + StringUtils.join(args, ",")))
                .append(paragraph("Method: " + method.toString()))
                .append(paragraph("Headers:"))
                .append(list(headers))
                .append(paragraph("Params:"))
                .append(list(params))
                .append(paragraph("Files:"))
                .append(list(files));

        return responseBody.toString();
    }

    @Override
    public String getTitle()
    {
        return "TotalFreedomMod :: Request Debug Dumper";
    }
}
