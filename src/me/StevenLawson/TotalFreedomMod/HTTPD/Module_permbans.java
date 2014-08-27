package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.File;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

public class Module_permbans extends TFM_HTTPD_Module
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
            return TFM_HTTPD_Manager.serveFileBasic(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PERMBAN_FILENAME));
        }
        else
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "Error 404: Not Found - The requested resource was not found on this server.");
        }
    }
}
