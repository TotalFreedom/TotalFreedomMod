package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.MIME_PLAINTEXT;
import me.StevenLawson.TotalFreedomMod.HTTPD.NanoHTTPD.Response;
import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

public class TFM_HTTPD_Manager
{
    private static final Pattern EXT_REGEX = Pattern.compile("\\.([^\\.\\s]+)$");
    //
    public static final int PORT = TFM_ConfigEntry.HTTPD_PORT.getInteger();
    //
    private final TFM_HTTPD httpd = new TFM_HTTPD(PORT);

    private TFM_HTTPD_Manager()
    {
    }

    public void start()
    {
        if (!TFM_ConfigEntry.HTTPD_ENABLED.getBoolean())
        {
            return;
        }

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
        if (!TFM_ConfigEntry.HTTPD_ENABLED.getBoolean())
        {
            return;
        }

        httpd.stop();

        TFM_Log.info("TFM HTTPd stopped.");
    }

    private static enum ModuleType
    {
        DUMP(false, "dump"),
        HELP(true, "help"),
        LIST(true, "list"),
        FILE(false, "file"),
        SCHEMATIC(false, "schematic");
        private final boolean runOnBukkitThread;
        private final String name;

        private ModuleType(boolean runOnBukkitThread, String name)
        {
            this.runOnBukkitThread = runOnBukkitThread;
            this.name = name;
        }

        public boolean isRunOnBukkitThread()
        {
            return runOnBukkitThread;
        }

        public String getName()
        {
            return name;
        }

        private static ModuleType getByName(String needle)
        {
            for (ModuleType type : values())
            {
                if (type.getName().equalsIgnoreCase(needle))
                {
                    return type;
                }
            }
            return FILE;
        }
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
        public Response serve(
                final String uri,
                final Method method,
                final Map<String, String> headers,
                final Map<String, String> params,
                final Map<String, String> files,
                final Socket socket)
        {
            Response response = null;

            final String[] args = StringUtils.split(uri, "/");
            final ModuleType moduleType = args.length >= 1 ? ModuleType.getByName(args[0]) : ModuleType.FILE;

            if (moduleType.isRunOnBukkitThread())
            {
                Future<Response> responseCall = Bukkit.getScheduler().callSyncMethod(TotalFreedomMod.plugin, new Callable<Response>()
                {
                    @Override
                    public Response call() throws Exception
                    {
                        switch (moduleType)
                        {
                            case HELP:
                                return new Module_help(uri, method, headers, params, files, socket).getResponse();
                            case LIST:
                                return new Module_list(uri, method, headers, params, files, socket).getResponse();
                            default:
                                return null;
                        }
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
            else
            {
                switch (moduleType)
                {
                    case DUMP:
                        //response = new Module_dump(uri, method, headers, params, files, socket).getResponse();
                        response = new Response(Response.Status.OK, MIME_PLAINTEXT, "The DUMP module is disabled. It is intended for debugging use only.");
                        break;
                    case SCHEMATIC:
                        response = new Module_schematic(uri, method, headers, params, files, socket).getResponse();
                        break;
                    default:
                        response = new Module_file(uri, method, headers, params, files, socket).getResponse();
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

    public static Response serveFileBasic(File file)
    {
        Response response = null;

        if (file != null && file.exists())
        {
            try
            {
                String mimetype = null;

                Matcher matcher = EXT_REGEX.matcher(file.getCanonicalPath());
                if (matcher.find())
                {
                    mimetype = Module_file.MIME_TYPES.get(matcher.group(1));
                }

                if (mimetype == null || mimetype.trim().isEmpty())
                {
                    mimetype = NanoHTTPD.MIME_DEFAULT_BINARY;
                }

                response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, mimetype, new FileInputStream(file));
                response.addHeader("Content-Length", "" + file.length());
            }
            catch (IOException ex)
            {
                TFM_Log.severe(ex);
            }
        }

        return response;
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
