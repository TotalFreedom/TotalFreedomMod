package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.HTTPSession;
import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.Method;
import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.Response;
import me.StevenLawson.TotalFreedomMod.TFM_Log;

public abstract class TFM_HTTPD_Module
{
    protected final String uri;
    protected final Method method;
    protected final Map<String, String> headers;
    protected final Map<String, String> params;
    protected final Socket socket;
    protected final HTTPSession session;

    public TFM_HTTPD_Module(HTTPSession session)
    {
        this.uri = session.getUri();
        this.method = session.getMethod();
        this.headers = session.getHeaders();
        this.params = session.getParms();
        this.socket = session.getSocket();
        this.session = session;
    }

    public String getBody()
    {
        return null;
    }

    public String getTitle()
    {
        return null;
    }

    public String getStyle()
    {
        return null;
    }

    public String getScript()
    {
        return null;
    }

    public Response getResponse()
    {
        return new TFM_HTTPD_PageBuilder(getBody(), getTitle(), getStyle(), getScript()).getResponse();
    }

    protected final Map<String, String> getFiles()
    {
        Map<String, String> files = new HashMap<String, String>();

        try
        {
            session.parseBody(files);
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }

        return files;
    }
}
