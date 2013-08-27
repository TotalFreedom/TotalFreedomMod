package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import me.StevenLawson.TotalFreedomMod.TFM_Log;

public class TFM_HTTPDManager
{
    public static final int PORT = 28966;
    //
    private final TFM_HTTPD httpd = new TFM_HTTPD(PORT);

    private TFM_HTTPDManager()
    {
    }

    public void start()
    {
        try
        {
            httpd.start();
        }
        catch (IOException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public void stop()
    {
        httpd.stop();
    }

    private static class TFM_HTTPD extends NanoHTTPD
    {
        public TFM_HTTPD(int port)
        {
            super(port);
        }

        public TFM_HTTPD(String hostname, int port)
        {
            super(hostname, port);
        }

        @Override
        public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files)
        {
            return new Response("<p>OK - " + new Date().toString() + "</p>");
        }
    }

    public static TFM_HTTPDManager getInstance()
    {
        return TFM_HTTPDManagerHolder.INSTANCE;
    }

    private static class TFM_HTTPDManagerHolder
    {
        private static final TFM_HTTPDManager INSTANCE = new TFM_HTTPDManager();
    }
}
