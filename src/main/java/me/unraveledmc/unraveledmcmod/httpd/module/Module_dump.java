package me.unraveledmc.unraveledmcmod.httpd.module;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import static me.unraveledmc.unraveledmcmod.httpd.HTMLGenerationTools.paragraph;
import me.unraveledmc.unraveledmcmod.httpd.HTTPDaemon;
import me.unraveledmc.unraveledmcmod.httpd.NanoHTTPD;
import me.unraveledmc.unraveledmcmod.util.FLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import static me.unraveledmc.unraveledmcmod.httpd.HTMLGenerationTools.list;

public class Module_dump extends HTTPDModule
{

    private File echoFile = null;
    private final String body;

    public Module_dump(UnraveledMCMod plugin, NanoHTTPD.HTTPSession session)
    {
        super(plugin, session);

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
            return HTTPDaemon.serveFileBasic(this.echoFile);
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
                    FLog.severe(ex);
                }
            }
        }

        return responseBody.toString();
    }

    @Override
    public String getTitle()
    {
        return "UnraveledMCMod :: Request Debug Dumper";
    }
}
