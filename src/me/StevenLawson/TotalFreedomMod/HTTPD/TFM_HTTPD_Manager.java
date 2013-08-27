package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

public class TFM_HTTPD_Manager
{
    public static final int PORT = 8748;
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

            if (httpd.isAlive())
            {
                TFM_Log.info("TFM HTTPd started. Listening on port: " + httpd.getListeningPort());
            }
            else
            {
                TFM_Log.info("Error starting TFM HTTPd.");
            }
        }
        catch (IOException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public void stop()
    {
        httpd.stop();

        TFM_Log.info("TFM HTTPd stopped.");
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
        public Response serve(final String uri, final Method method, final Map<String, String> headers, final Map<String, String> params, final Map<String, String> files)
        {
            Response response = null;

            final String[] args = StringUtils.split(uri, "/");
            if (args.length >= 1)
            {
                Future<Response> responseCall = Bukkit.getScheduler().callSyncMethod(TotalFreedomMod.plugin, new Callable<Response>()
                {
                    @Override
                    public Response call() throws Exception
                    {
                        if ("dump".equalsIgnoreCase(args[0]))
                        {
                            return new Module_dump(uri, method, headers, params, files).getResponse();
                        }
                        else if ("list".equalsIgnoreCase(args[0]))
                        {
                            return new Module_list(uri, method, headers, params, files).getResponse();
                        }
                        else if ("help".equalsIgnoreCase(args[0]))
                        {
                            return new Module_help(uri, method, headers, params, files).getResponse();
                        }
                        else if ("public".equalsIgnoreCase(args[0]))
                        {
                            return new Module_file(uri, method, headers, params, files).getResponse();
                        }
                        return null;
                    }
                });

                try
                {
                    response = responseCall.get();
                }
                catch (Exception ex)
                {
                    TFM_Log.severe(ex);
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
