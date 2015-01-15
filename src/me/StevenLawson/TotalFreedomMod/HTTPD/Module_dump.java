package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import static me.StevenLawson.TotalFreedomMod.HTTPD.HTMLGenerationTools.list;
import static me.StevenLawson.TotalFreedomMod.HTTPD.HTMLGenerationTools.paragraph;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class Module_dump extends TFM_HTTPD_Module
{
    private File echoFile = null;
    private final String body;

    public Module_dump(NanoHTTPD.HTTPSession session)
    {
        super(session);

        //Body needs to be computed before getResponse, so we know if a text response or a file echo is needed.
        this.body = body();
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
        String echo = params.get("echo");
        boolean doEcho = echo != null && ((echo = echo.toLowerCase().trim()).equalsIgnoreCase("true") || echo.equalsIgnoreCase("1"));

        if (doEcho && this.echoFile != null && this.echoFile.exists())
        {
            return TFM_HTTPD_Manager.serveFileBasic(this.echoFile);
        }
        else
        {
            return super.getResponse();
        }
    }

    @Override
    public String getBody()
    {
        return body;
    }

    private String body()
    {
        StringBuilder responseBody = new StringBuilder();

        String remoteAddress = socket.getInetAddress().getHostAddress();

        String[] args = StringUtils.split(uri, "/");

        Map<String, String> files = getFiles();

        responseBody
                .append(paragraph("URI: " + uri))
                .append(paragraph("args (Length: " + args.length + "): " + StringUtils.join(args, ",")))
                .append(paragraph("Method: " + method.toString()))
                .append(paragraph("Remote Address: " + remoteAddress))
                .append(paragraph("Headers:"))
                .append(list(headers))
                .append(paragraph("Params:"))
                .append(list(params))
                .append(paragraph("Files:"))
                .append(list(files));

        Iterator<Map.Entry<String, String>> it = files.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, String> entry = it.next();
            String formName = entry.getKey();
            String tempFileName = entry.getValue();
            String origFileName = params.get(formName);

            File tempFile = new File(tempFileName);
            if (tempFile.exists())
            {
                this.echoFile = tempFile;

                if (origFileName.contains("../"))
                {
                    continue;
                }

                String targetFileName = "./public_html/uploads/" + origFileName;

                File targetFile = new File(targetFileName);

                try
                {
                    FileUtils.copyFile(tempFile, targetFile);
                }
                catch (IOException ex)
                {
                    TFM_Log.severe(ex);
                }
            }
        }

        return responseBody.toString();
    }

    @Override
    public String getTitle()
    {
        return "TotalFreedomMod :: Request Debug Dumper";
    }
}
