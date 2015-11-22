package me.totalfreedom.totalfreedommod.httpd;

import java.io.File;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;

public class Module_permbans extends HTTPDModule
{

    public Module_permbans(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
        File permbanFile = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PERMBAN_FILENAME);
        if (permbanFile.exists())
        {
            return HTTPDaemon.serveFileBasic(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PERMBAN_FILENAME));
        }
        else
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "Error 404: Not Found - The requested resource was not found on this server.");
        }
    }
}
