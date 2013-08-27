package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.IOException;
import java.util.Map;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import org.apache.commons.lang.StringUtils;

public class TFM_HTTPD_Manager
{
    public static final int PORT = 28966;
    //
    private final TFM_HTTPD httpd = new TFM_HTTPD(PORT);

    private TFM_HTTPD_Manager()
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
        public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files)
        {
            Response response = null;

            final String[] args = StringUtils.split(uri, "/");
            if (args.length >= 1)
            {
                if ("dump".equalsIgnoreCase(args[0]))
                {
                    response = new Module_dump(uri, method, headers, params, files).getResponse();
                }
                else if ("list".equalsIgnoreCase(args[0]))
                {
                    response = new Module_list(uri, method, headers, params, files).getResponse();
                }
                else if ("help".equalsIgnoreCase(args[0]))
                {
                    //The issue is that plugin.getDescription().getCommands() only shows commands in the plugin.yml file.
                    //I need to make another version of this that uses the CommandMap.
                    response = new Module_help(uri, method, headers, params, files).getResponse();
                }
            }

            if (response == null)
            {
                return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Error 404: Not Found - The requested resource was not found on this server.");
            }
            else
            {
                return response;
            }
        }
    }

    public static TFM_HTTPD_Manager getInstance()
    {
        return TFM_HTTPDManagerHolder.INSTANCE;
    }

    private static class TFM_HTTPDManagerHolder
    {
        private static final TFM_HTTPD_Manager INSTANCE = new TFM_HTTPD_Manager();
    }
}
