package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.util.Map;
import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.*;

public abstract class TFM_HTTPD_Module
{
    protected final String uri;
    protected final Method method;
    protected final Map<String, String> headers;
    protected final Map<String, String> params;
    protected final Map<String, String> files;

    public TFM_HTTPD_Module(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files)
    {
        this.uri = uri;
        this.method = method;
        this.headers = headers;
        this.params = params;
        this.files = files;
    }

    public String getBody()
    {
        return "";
    }

    public String getTitle()
    {
        return "";
    }

    public String getStyle()
    {
        return "";
    }

    public Response getResponse()
    {
        return new TFM_HTTPD_PageBuilder(getBody(), getTitle(), getStyle()).getResponse();
    }
}
