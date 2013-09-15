package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.File;
import java.net.Socket;
import java.util.Map;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

public class Module_permbans extends TFM_HTTPD_Module
{
    public Module_permbans(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files, Socket socket)
    {
        super(uri, method, headers, params, files, socket);
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
        File permbanFile = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PERMBAN_FILE);
        if (permbanFile.exists())
        {
            return TFM_HTTPD_Manager.serveFileBasic(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PERMBAN_FILE));
        }
        else
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "Error 404: Not Found - The requested resource was not found on this server.");
        }
    }
}
