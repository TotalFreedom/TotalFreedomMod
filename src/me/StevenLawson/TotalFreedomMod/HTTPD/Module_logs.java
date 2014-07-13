package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.File;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;

public class Module_logs extends Module_file
{
    public Module_logs(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
        if (TFM_ConfigEntry.LOGS_SECRET.getString().equals(params.get("password")))
        {
            return serveFile("latest.log", params, new File("./logs"));
        }
        else
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "Incorrect password.");
        }
    }
}
